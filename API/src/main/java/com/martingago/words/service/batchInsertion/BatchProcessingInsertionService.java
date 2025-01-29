package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.qualification.WordQualificationService;
import com.martingago.words.service.word.BatchWordInsertionService;
import com.martingago.words.utils.BatchUtils;
import com.martingago.words.utils.WordListDefinitionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
public class BatchProcessingInsertionService {

    private static final int BATCH_SIZE = 50;

    @Autowired
    BatchWordInsertionService batchWordInsertionService;

    @Autowired
    BatchInsertionQualificationService batchInsertionQualificationService;

    @Autowired
    LanguageService languageService;

    @Autowired
    WordQualificationService wordQualificationService;

    @Autowired
    WordListDefinitionUtils wordListDefinitionUtils;

    @Autowired
    BatchInsertionDefinitionService batchInsertionDefinitionService;

    /**
     * Procesa un fichero .json que tiene un listado de palabras.
     * Por cada batch realiza una inserción de 50 palabras
     * @param allWords
     */
    @Transactional
    public void processJsonFile(Map<String, WordResponseDTO> allWords) {
        int totalWords = allWords.size();
        final int[] processedWords = {0};

        //Obtener el listado de idiomas existente en la BBDD y generar un map
        Map<String, LanguageModel> mappedLanguages = languageService.getAllLanguagesMappedByLangCode();

        //Obtener el listado de qualifications existentes en la BBDD y generar un map
        Map<String, WordQualificationModel> mappedQualifications = wordQualificationService.getAllQualificationsMapped();

        // Procesar en lotes utilizando BatchUtils
        BatchUtils.processMapInBatches(allWords, BATCH_SIZE, batch -> {
            try {
                // Insertar el lote actual de palabras y el mappeo de idiomas para evitar consultas por cada batch
                Map<String, WordModel> insertedWords = batchWordInsertionService.insertBatchWordsMap(batch, mappedLanguages);

                //Genera un map que contiene las keys palabras/placeholders que se van a insertar asociados wordModel + definitionDTO
                Map<String, WordListDefinitionsPojo> wordListDefinitionsPojoMap = wordListDefinitionUtils.getCommonWordsWithDefinitions(insertedWords, batch);

                //Insertar por batches las qualification de las palabras:
                Map<String, WordQualificationModel> insertedQualifications = batchInsertionQualificationService.insertBatchWordQualificationMap(
                        wordListDefinitionsPojoMap, mappedQualifications);

                //Actualiza el listado de qualifications con las nuevas qualifications añadidas:
                mappedQualifications.putAll(insertedQualifications);

                //Insertar por batches las Definitions de las palabras
                batchInsertionDefinitionService.insertBatchWordDefinitionMap(wordListDefinitionsPojoMap,mappedQualifications);

                //Insertar por lotes las qualifications
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