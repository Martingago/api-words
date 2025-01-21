package com.martingago.words.mapper;

import com.martingago.words.dto.WordDefinitionDTO;
import com.martingago.words.model.WordDefinitionModel;
import com.martingago.words.model.WordExampleModel;
import com.martingago.words.model.WordRelationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
                wordExamplesMapper.toStringList(wordDefinitionModel.getWordExampleModelList()),
                wordRelationMapper.mapSynonyms(wordDefinitionModel.getWordRelationModelList()),
                wordRelationMapper.mapAntonyms(wordDefinitionModel.getWordRelationModelList())
        );
    }

    /**
     * Convierte una lista de WordDefinitionModel en una lista de WordDefinitionDTOs
     * @param wordDefinitionModelList
     * @return
     */
    public List<WordDefinitionDTO> toDTOList(List<WordDefinitionModel> wordDefinitionModelList){
        if(wordDefinitionModelList == null) return List.of();
        return wordDefinitionModelList.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
