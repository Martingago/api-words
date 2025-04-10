package com.martingago.words.domain.service.word;

import com.martingago.words.context.DefinitionProcessedContext;
import com.martingago.words.domain.model.*;
import com.martingago.words.domain.repository.WordQualificationRepository;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            Map<String, WordQualificationModel> qualificationModelMap) {
        WordQualificationModel newQualificationModelAdded = null; //En caso que se cree una nueva qualification
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
     * Crea un Set de ejemplos a partir de una WordDefinitionDTO
     * @param dto
     * @param def
     * @return
     */
    public Set<WordExampleModel> createExamples(WordDefinitionDTO dto, WordDefinitionModel def) {
        if (dto.getExamples() == null) return Set.of();
        return dto.getExamples().stream().map(text -> {
            WordExampleModel ex = new WordExampleModel();
            ex.setExample(text);
            ex.setWordDefinitionModel(def);
            return ex;
        }).collect(Collectors.toSet());
    }

    /**
     *
     * @param relatedWords
     * @param def
     * @param type
     * @param wordProvider
     * @return
     */
    public Set<WordRelationModel> buildRelations(
            Set<String> relatedWords,
            WordDefinitionModel def,
            RelationEnumType type,
            Function<String, WordModel> wordProvider // dynamic supplier from batch or db
    ) {
        if (relatedWords == null) return Set.of();
        return relatedWords.stream().map(word -> {
            WordRelationModel rel = new WordRelationModel();
            rel.setWordDefinitionModel(def);
            rel.setRelationEnumType(type);
            rel.setWordRelated(wordProvider.apply(word));
            return rel;
        }).collect(Collectors.toSet());
    }



}
