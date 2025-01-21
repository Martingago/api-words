package com.martingago.words.mapper;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.model.WordRelationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WordDefinitionMapper {

    @Autowired
    WordExamplesMapper wordExamplesMapper;

    @Autowired
    WordRelationMapper wordRelationMapper;

    /**
     * Convierte un WordDefinitionModel en un WordDefinitionDTO. Para ello se desestructura la siguiente información:
     * → Convierte el list de examplesModel en un list de Strings
     * → Convierte el listado de WordRelationModel en un listado de palabras sinónimas (String word)
     * → Convierte el listado de WordRelationModel en un listado de palabras antónimas (String word)
     * @param wordDefinitionModel
     * @return
     */
    public WordDefinitionDTO toDTO(WordDefinitionModel wordDefinitionModel){
        if(wordDefinitionModel == null) return null;
        return new WordDefinitionDTO(
                wordDefinitionModel.getWordQualificationModel().getQualification(),
                wordDefinitionModel.getWordDefinition(),
                wordExamplesMapper.toStringSet(wordDefinitionModel.getWordExampleModelSet()),
                wordRelationMapper.mapSynonyms(wordDefinitionModel.getWordRelationModelSet()),
                wordRelationMapper.mapAntonyms(wordDefinitionModel.getWordRelationModelSet())
        );
    }

    /**
     * Convierte una lista de WordDefinitionModel en una lista de WordDefinitionDTOs
     * @param wordDefinitionModelSet
     * @return
     */
    public Set<WordDefinitionDTO> toDTOSet(Set<WordDefinitionModel> wordDefinitionModelSet){
        if(wordDefinitionModelSet == null) return Set.of();
        return wordDefinitionModelSet.stream().map(this::toDTO).collect(Collectors.toSet());
    }
}
