package com.martingago.words.domain.service.definition;

import com.martingago.words.context.DefinitionProcessedContext;
import com.martingago.words.domain.model.WordDefinitionModel;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.repository.WordQualificationRepository;
import com.martingago.words.domain.service.example.CreateExampleService;
import com.martingago.words.domain.service.relation.CreateWordRelationsService;
import com.martingago.words.dto.models.definition.WordDefinitionDTO;
import com.martingago.words.dto.models.word.request.SimpleWordSerializableDTO;
import com.martingago.words.dto.models.word.response.WordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CreateDefinitionService {

    private final WordQualificationRepository wordQualificationRepository;
    private final CreateExampleService createExampleService;
    private final CreateWordRelationsService createWordRelationsService;


    /**
     * Función que se encarga de crear un set de WordDefinitionModel
     * @param dto que contiene la información global de la palabra que se quiere añadir (Listado definiciones)
     * @param wordModel modelo que estará asociado con las palabras que vamos a crear.
     * @param qualificationMap Map que contiene la información de las definiciones existentes en la Base de datos.
     * @param newWordsModelToPersist Palabras relacionadas acumuladas (placeholders) que van a ser persistidas en la inserción.
     * @param existingDBWordsMap Referencias de las palabras que ya existen en la base de datos
     */
    public void processDefinitions(WordDTO dto,
                                   WordModel wordModel,
                                   Map<String, WordQualificationModel> qualificationMap,
                                   Map<String, WordModel> newWordsModelToPersist,
                                   Map<String, SimpleWordSerializableDTO> existingDBWordsMap
    ) {
        //wordModel.getWordDefinitionModelSet().clear();

        Set<WordDefinitionModel> definitionModelSetToAdd = new HashSet<>();

        if (dto.getDefinitions() != null && !dto.getDefinitions().isEmpty()) {
            for (WordDefinitionDTO defDto : dto.getDefinitions()) {

                //Crea la definición de una palabra y devuelve su objeto model + Qualification creada (opcional)
                DefinitionProcessedContext definitionProcessedContext = createWordDefinition(defDto, wordModel,qualificationMap);

                WordDefinitionModel wordDefinitionModel = definitionProcessedContext.definitionModel();

                // Verificamos si se ha creado una nueva qualification y la añadimos al mapa
                if (definitionProcessedContext.hasNewQualification()) {
                    WordQualificationModel newQualification = definitionProcessedContext.newQualification().get();
                    qualificationMap.put(newQualification.getQualification(), newQualification);
                }

                //Se añade la definición al wordModel
                //wordModel.getWordDefinitionModelSet().add(wordDefinitionModel);

                definitionModelSetToAdd.add(wordDefinitionModel);
                //Se procesan los ejemplos relacionados con una definicion
                createExampleService.processExamples(defDto, wordDefinitionModel);
                //Procesa las relaciones de sinonimos.
                createWordRelationsService.processSynonyms(defDto, wordDefinitionModel, newWordsModelToPersist, existingDBWordsMap);
                //Procesa las relaciones de antónimos.
                createWordRelationsService.processAntonyms(defDto, wordDefinitionModel, newWordsModelToPersist, existingDBWordsMap);
            }
            wordModel.setWordDefinitionModelSet(definitionModelSetToAdd);
        }
    }


    /**
     * Crea una (1) WordDefinitionModel para una palabra
     * @param defDto DTO que contiene la información de la definición a crear en la Base de datos.
     * @param wordModel palabra con la que está asociada dicha definición.
     * @param qualificationModelMap map que contiene las qualificaciones existentes en la Base de datos.
     * @return DefinitionProcessedContext: contiene WordDefinitionModel + WordQualification creada (opcional)
     */
    public DefinitionProcessedContext createWordDefinition(
            WordDefinitionDTO defDto,
            WordModel wordModel,
            Map<String, WordQualificationModel> qualificationModelMap
    ) {
        WordQualificationModel newQualificationModelAdded = null; //En caso de que se cree una nueva qualification
        WordDefinitionModel wordDefinitionModel = new WordDefinitionModel();
        wordDefinitionModel.setWordDefinition(defDto.getDefinition());

        //Comprueba del map de qualificaciones recibidas, si la qualificación existe o no en la base de datos.
        WordQualificationModel qualificationModel = qualificationModelMap.get(defDto.getQualification());

        if (qualificationModel == null) {
            qualificationModel = new WordQualificationModel();
            qualificationModel.setQualification(defDto.getQualification());
            //Guarda la qualification nueva
            qualificationModel = wordQualificationRepository.save(qualificationModel);
            newQualificationModelAdded = qualificationModel;
        }
        //Establece la qualification
        wordDefinitionModel.setWordQualificationModel(qualificationModel);

        //Establece la palabra
        wordDefinitionModel.setWord(wordModel);

        //Crea el objeto con la información de la definición creada
        return DefinitionProcessedContext.builder()
                .definitionModel(wordDefinitionModel)
                .newQualification(Optional.ofNullable(newQualificationModelAdded))
                .build();
    }
}
