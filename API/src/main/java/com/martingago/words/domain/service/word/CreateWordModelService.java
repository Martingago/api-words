package com.martingago.words.domain.service.word;

import com.martingago.words.domain.model.*;
import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.dto.word.request.WordBatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CreateWordModelService {

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
     * Crea una definición a través de un WordDefinitionDTO
     * @param dto
     * @param word
     * @param qualification
     * @return
     */
    public WordDefinitionModel createDefinition(
            WordDefinitionDTO dto, WordModel word, WordQualificationModel qualification
    ) {
        WordDefinitionModel def = new WordDefinitionModel();
        def.setWord(word);
        def.setWordDefinition(dto.getDefinition());
        def.setWordQualificationModel(qualification);
        return def;
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
