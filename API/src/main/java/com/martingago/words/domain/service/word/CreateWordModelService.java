package com.martingago.words.domain.service.word;

import com.martingago.words.context.DefinitionProcessedContext;
import com.martingago.words.domain.model.*;
import com.martingago.words.domain.repository.WordQualificationRepository;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class CreateWordModelService {

    private final WordQualificationRepository wordQualificationRepository;

    /**
     * Crea la entidad que se va a persistir en la base de datos de WordModel
     * @param dto información del objeto que se quiere persistir en la BBDD.
     * @return objeto persistido en la BDDD.
     */
    public WordModel createWord(WordBatchDTO dto, LanguageModel languageModel) {
        WordModel wordModel = new WordModel();
        wordModel.setWord(dto.getWord());
        wordModel.setLength(dto.getLength());
        wordModel.setPlaceholder(false);

        if (languageModel == null) {
            return null;
        }
        wordModel.setLanguageModel(languageModel);
        return wordModel;
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

    /**
     * Añade a la base de datos los ejemplos existentes en un wordDefinitionDTO asociados a una entidad WordDefinitionModel
     * @param defDto WordDefinitionDTO sobre el que se van a extraer el listado de ejemplos para ser añadidos a la BBDD
     * @param wordDefinitionModel al que están asociados los ejemplos que se van a añadir a la base de datos.
     */
    public void processExamples(WordDefinitionDTO defDto, WordDefinitionModel wordDefinitionModel) {
        Set<WordExampleModel> examples = new HashSet<>();
        if (defDto.getExamples() != null && !defDto.getExamples().isEmpty()) {
            for (String ex : defDto.getExamples()) {
                WordExampleModel example = new WordExampleModel();
                example.setExample(ex);
                example.setWordDefinitionModel(wordDefinitionModel);
                examples.add(example);
            }
        }
        wordDefinitionModel.setWordExampleModelSet(examples);
    }






}
