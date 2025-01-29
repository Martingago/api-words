package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.DefinitionEstructurePojo;
import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class BatchInsertionDefinitionService {

    @Autowired
    WordDefinitionRepository wordDefinitionRepository;

    @Transactional
    public Map<String, DefinitionEstructurePojo> insertBatchWordDefinitionMap(
            Map<String, WordListDefinitionsPojo> stringWordListDefinitionsPojoMap,
            Map<String, WordQualificationModel> mappedQualifications
    ) {
        //Map que contiene la información de las definiciones que han sido añadidas en la BBDD.
        Map<String, DefinitionEstructurePojo> insertedWordDefinitionsMap = new HashMap<>();

        BatchUtils.processMapInBatches(stringWordListDefinitionsPojoMap, 50, batch -> {
            try {
                Set<WordDefinitionModel> definitionModelSet = new HashSet<>();

                // Recorremos el lote de palabras y sus definiciones
                for (Map.Entry<String, WordListDefinitionsPojo> entry : batch.entrySet()) {
                    WordListDefinitionsPojo pojo = entry.getValue();

                    // Recorremos el Set de definiciones de cada palabra
                    for (var definitionDTO : pojo.getWordDefinitionDTOSet()) {
                        String qualification = definitionDTO.getQualification();

                        // Verifica si la qualification existe en el map de qualifications
                        WordQualificationModel qualificationModel = mappedQualifications.get(qualification);
                        if (qualificationModel == null) continue;

                        // Creamos el modelo de definición de palabra
                        WordDefinitionModel definitionModel = WordDefinitionModel.builder()
                                .word(pojo.getWordModel())// Aquí se pasa la palabra
                                .wordDefinition(definitionDTO.getDefinition())
                                .wordQualificationModel(qualificationModel)
                                // Agrega más campos según tu estructura
                                .build();

                        // Añadimos la definición al Set
                        definitionModelSet.add(definitionModel);
                    }
                }

                // Realizamos la inserción en la base de datos
                if (!definitionModelSet.isEmpty()) {
                    wordDefinitionRepository.saveAll(definitionModelSet);
                }

                //Insertar batch de ejemplos de las palabras


            } catch (Exception e) {
                log.error("Error processing definitions batch: {}", e.getMessage(), e);
            }
        });

        return insertedWordDefinitionsMap;
    }
}
