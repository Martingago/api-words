package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.qualification.WordQualificationService;
import com.martingago.words.utils.BatchUtils;
import com.martingago.words.utils.WordListDefinitionUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * Función que recibe un fichero .json y se encarga de dividirlo en lotes y manejarlos individualmente
     * @param allWords
     */
    public void processAllJsonData(Map<String, WordResponseDTO> allWords) {
        int totalWords = allWords.size();
        final int[] processedWords = {0};

        // Obtener los idiomas existentes
        Map<String, LanguageModel> mappedLanguages = languageService.getAllLanguagesMappedByLangCode();

        // Obtener qualifications iniciales en un mapa mutable
        Map<String, WordQualificationModel> mappedQualifications = new ConcurrentHashMap<>(wordQualificationService.getAllQualificationsMapped());

        BatchUtils.processMapInBatches(allWords, BATCH_SIZE, batch -> {
            try {
                // Llamar a un método transaccional para procesar el batch
                processBatchWordsTransactional(batch, mappedLanguages, mappedQualifications);

                // Log del progreso
                processedWords[0] += batch.size();
                log.info("Processed batch: {} words inserted (Total processed: {}/{})",
                        batch.size(), processedWords[0], totalWords);
            } catch (Exception e) {
                // Manejo de errores
                log.error("Error processing batch. Transaction rolled back for this batch. Reason: {}", e.getMessage(), e);
            }
        });
        log.info("Finished processing {} words out of {}", processedWords[0], totalWords);
    }

    /**
     * Operación que maneja la creación de palabras y definiciones de un lote de 50 transaccional de palabras
     * @param batch Map con el WordResponseDTO a insertar en la base de datos batcheado a un tamaño determinado
     * @param mappedLanguages mapeo de los idiomas existentes en la BBDD para validar la integridad de las palabras
     * @param mappedQualifications mapeo de las qualificaciones existentes en la BBDD asociadas a las palabras a añadir.
     */
    @Transactional
    public void processBatchWordsTransactional(Map<String, WordResponseDTO> batch,
                                               Map<String, LanguageModel> mappedLanguages,
                                               Map<String, WordQualificationModel> mappedQualifications) {

        // Insertar palabras
        Map<String, WordModel> insertedWords = batchWordInsertionService.insertBatchWordsMap(batch, mappedLanguages);

        // Generar el mapeo de palabras con sus definiciones
        Map<String, WordListDefinitionsPojo> wordListDefinitionsPojoMap = wordListDefinitionUtils.getCommonWordsWithDefinitions(insertedWords, batch);

        // Insertar qualifications para las palabras del batch y actualizar el mapa compartido
        Map<String, WordQualificationModel> insertedQualifications = batchInsertionQualificationService.insertBatchWordQualificationMap(
                wordListDefinitionsPojoMap, mappedQualifications
        );
        mappedQualifications.putAll(insertedQualifications);

        // Insertar definiciones del lote
        if (!insertedWords.isEmpty()) {
            batchInsertionDefinitionService.insertBatchWordDefinitionMap(wordListDefinitionsPojoMap, mappedQualifications);
        }
    }
}
