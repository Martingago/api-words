package com.martingago.words.service.batchInsertion;

import com.martingago.words.POJO.DefinitionEstructurePojo;
import com.martingago.words.POJO.WordListDefinitionsPojo;
import com.martingago.words.POJO.WordRelationPojo;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordQualificationModel;
import com.martingago.words.repository.WordDefinitionRepository;
import com.martingago.words.utils.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BatchInsertionDefinitionService {

    @Autowired
    WordDefinitionRepository wordDefinitionRepository;

    @Autowired
    BatchesInsertionExamplesService batchesInsertionExamplesService;

    @Autowired
    BatchInsertionRelationService batchInsertionRelationService;

    /**
     * Función que recibe un map
     * @param stringWordListDefinitionsPojoMap mapa que contiene como key la palabra, y como objeto un WordListDefinitionsPojo
     * @param mappedQualifications mapa que tiene como key una qualification, y como objeto un WordQualificationModel
     */
    @Transactional
    public void insertBatchWordDefinitionMap(
            Map<String, WordListDefinitionsPojo> stringWordListDefinitionsPojoMap,
            Map<String, WordQualificationModel> mappedQualifications
    ) {

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

                // Guarda los datos de las definiciones en la Base de datos y añade los ejemplos y relaciones
                if (!definitionModelSet.isEmpty()) {
                    List<WordDefinitionModel> insertedDefinitionsIntoDatabase = wordDefinitionRepository.saveAll(definitionModelSet);

                    //Genera un Set que contiene la información extra relacionada con las definiciones: Ejemplos y relaciones.
                    Set<DefinitionEstructurePojo> setDefinicionesPOJO = buildDefinitionEstructurePojos(insertedDefinitionsIntoDatabase, stringWordListDefinitionsPojoMap);

                    //Insertar batch de ejemplos de las palabras
                    batchesInsertionExamplesService.insertBatchExamplesList(setDefinicionesPOJO);

                    // Insertar batch de relaciones de las palabras
                    batchInsertionRelationService.insertBatchRelationSet(setDefinicionesPOJO);
                }

            } catch (Exception e) {
                log.error("Error processing definitions batch: {}", e.getMessage(), e);
            }
        });

    }

    /**
     * Devuelve una lista que contiene un POJO con la información de: definicion palabra + Set<String> ejemplos
     *
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

                //Inserta las relaciones existentes con otras palabras (SINONIMA/ANTONIMA):
                List<WordRelationPojo> wordRelation = wordListPojo.getWordDefinitionDTOSet().stream()
                        .filter(dto -> dto.getDefinition().equals(definitionModel.getWordDefinition()))
                        .findFirst()
                        .map(list -> list.getSynonyms().stream()
                                .map(syn -> WordRelationPojo.builder()
                                        .word(syn)
                                        .relationEnumType(RelationEnumType.SINONIMA)
                                        .build())
                                .collect(Collectors.toList())
                        ).orElse(new ArrayList<>());

                wordListPojo.getWordDefinitionDTOSet().stream()
                        .filter(dto -> dto.getDefinition().equals(definitionModel.getWordDefinition()))
                        .findFirst()
                        .ifPresent(dto -> wordRelation.addAll(dto.getAntonyms().stream()
                                .map(ant -> WordRelationPojo.builder()
                                        .word(ant)
                                        .relationEnumType(RelationEnumType.ANTONIMA)
                                        .build())
                                .collect(Collectors.toList())));

                // Solo agregar si hay datos relevantes
                if (!examples.isEmpty() || !wordRelation.isEmpty()) {
                    DefinitionEstructurePojo pojo = DefinitionEstructurePojo.builder()
                            .wordDefinitionModel(definitionModel)
                            .listExamples(examples)
                            .relationPojoList(wordRelation)
                            .build();

                    definitionEstructurePojos.add(pojo);
                }
            }
        }
        return definitionEstructurePojos;
    }

}
