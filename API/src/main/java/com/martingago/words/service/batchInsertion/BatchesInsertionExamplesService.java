package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.DefinitionEstructurePojo;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.repository.WordExampleRepository;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BatchesInsertionExamplesService {

    @Autowired
    WordExampleRepository wordExampleRepository;

    /**
     * Sobre un listado de
     * @param setDefinicionesPojo
     * @return
     */
    public Set<WordExampleModel> insertBatchExamplesList(
            Set<DefinitionEstructurePojo> setDefinicionesPojo) {
        // Declaración del set a devolver con los ejemplos insertados:
        Set<WordExampleModel> insertedExamples = new HashSet<>();

        BatchUtils.processInBatches(setDefinicionesPojo, 50, batch -> {
            try {
                Set<WordExampleModel> examplesToInsert = new HashSet<>();

                // Recorre la lista de definiciones existentes:
                for (DefinitionEstructurePojo definitionStructure : batch) {
                    // De cada definición, extrae el set de ejemplos que puede contener
                    Set<WordExampleModel> examples = definitionStructure.getListExamples().stream()
                            .map(exampleModel -> WordExampleModel.builder()
                                    .example(exampleModel)
                                    .wordDefinitionModel(definitionStructure.getWordDefinitionModel())
                                    .build())
                            .collect(Collectors.toSet());

                    examplesToInsert.addAll(examples); // Añade el set de ejemplos por definición
                }

                // Realiza la inserción por batches:
                if (!examplesToInsert.isEmpty()) {
                    insertedExamples.addAll(wordExampleRepository.saveAll(examplesToInsert)); // Acumula en lugar de sobrescribir
                }

            } catch (Exception e) {
                log.error("Error processing example batch: {}", e.getMessage(), e);
            }
        });

        return insertedExamples;
    }
}
