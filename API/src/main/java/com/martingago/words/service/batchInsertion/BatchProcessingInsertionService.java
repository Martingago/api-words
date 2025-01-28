package com.martingago.words.service.batchInsertion;

import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.word.BatchWordInsertionService;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class BatchProcessingInsertionService {

    private static final int BATCH_SIZE = 50;

    @Autowired
    BatchWordInsertionService batchWordInsertionService;

    @Autowired
    LanguageService languageService;

    /**
     * Procesa un fichero .json que tiene un listado de palabras.
     * Por cada batch realiza una inserción de 50 palabras
     * @param allWords
     */
    @Transactional
    public void processJsonFile(Set<WordResponseDTO> allWords) {
        int totalWords = allWords.size();
        final int[] processedWords = {0};

        //Obtener el listado de idiomas existente en la BBDD y generar un map
        Map<String, LanguageModel> mappedLanguages = languageService.getAllLanguagesMappedByLangCode();

        // Procesar en lotes utilizando BatchUtils
        BatchUtils.processInBatches(allWords, BATCH_SIZE, batch -> {
            try {
                // Insertar el lote actual de palabras y el mappeo de idiomas para evitar consultas por cada batch
                Set<WordModel> insertedWords = batchWordInsertionService.insertBatchWordsSet(batch, mappedLanguages);
                processedWords[0] += insertedWords.size();

                // Log del progreso
                log.info("Processed batch: {} words inserted (Total processed: {}/{})",
                        insertedWords.size(), processedWords, totalWords);
            } catch (Exception e) {
                // Manejo de errores
                log.error("Error processing batch: {}", e.getMessage());
                // Aquí podrías implementar lógica de reintento o manejo de errores
            }
        });

        // Log final
        log.info("Finished processing {} words out of {}", processedWords, totalWords);
    }
}