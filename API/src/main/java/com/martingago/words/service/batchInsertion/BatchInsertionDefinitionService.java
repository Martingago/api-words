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

import java.util.*;

@Slf4j
@Service
public class BatchInsertionDefinitionService {

    @Autowired
    WordDefinitionRepository wordDefinitionRepository;

    @Autowired
    BatchesInsertionExamplesService batchesInsertionExamplesService;

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
                    List<WordDefinitionModel>  insertedDefinitionsIntoDatabase =  wordDefinitionRepository.saveAll(definitionModelSet);
                    Set<DefinitionEstructurePojo> setDefinicionesPOJO = buildDefinitionEstructurePojos(insertedDefinitionsIntoDatabase, stringWordListDefinitionsPojoMap);

                    // Llenar el mapa de salida con las definiciones insertadas
                    for (DefinitionEstructurePojo pojo : setDefinicionesPOJO) {
                        insertedWordDefinitionsMap.put(pojo.getWordDefinitionModel().getWord().getWord(), pojo);
                    }

                    //Insertar batch de ejemplos de las palabras
                    batchesInsertionExamplesService.insertBatchExamplesList(setDefinicionesPOJO);
                }

            } catch (Exception e) {
                log.error("Error processing definitions batch: {}", e.getMessage(), e);
            }
        });

        return insertedWordDefinitionsMap;
    }


    /**
     * Devuelve una lista que contiene un POJO con la información de: definicion palabra + Set<String> ejemplos
     * @param insertedDefinitions
     * @param stringWordListDefinitionsPojoMap
     * @return
     */
    private Set<DefinitionEstructurePojo> buildDefinitionEstructurePojos(
            List<WordDefinitionModel> insertedDefinitions,
            Map<String, WordListDefinitionsPojo> stringWordListDefinitionsPojoMap) {

        Set<DefinitionEstructurePojo> definitionEstructurePojos = new HashSet<>();

        for (WordDefinitionModel definitionModel : insertedDefinitions) {
            String word = definitionModel.getWord().getWord(); //De la definición insertada se obtiene la palabra

            WordListDefinitionsPojo wordListPojo = stringWordListDefinitionsPojoMap.get(word);
            if (wordListPojo != null) {
                // Buscar los ejemplos asociados a esta definición específica
                List<String> examples = wordListPojo.getWordDefinitionDTOSet().stream()
                        .filter(dto -> dto.getDefinition().equals(definitionModel.getWordDefinition()))
                        .findFirst()
                        .map(dto -> new ArrayList<>(dto.getExamples()))
                        .orElse(new ArrayList<>());

                //Genera el builder con el DefinitionStructurePojo
                DefinitionEstructurePojo pojo = DefinitionEstructurePojo.builder()
                        .wordDefinitionModel(definitionModel)
                        .listExamples(examples)
                        .build();

                definitionEstructurePojos.add(pojo);
            }
        }

        return definitionEstructurePojos;
    }

}
