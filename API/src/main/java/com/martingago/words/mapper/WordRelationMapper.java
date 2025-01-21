package com.martingago.words.mapper;

import com.martingago.words.dto.WordRelationDTO;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordRelationModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WordRelationMapper {

    /**
     * Genera un WordRelationDTO a partir de un WordRelationModel
     * @param wordRelationModel
     * @return
     */
    public WordRelationDTO toDTO(WordRelationModel wordRelationModel) {
        if (wordRelationModel == null) return null;
        return new WordRelationDTO
                (wordRelationModel.getRelationEnumType(),
                wordRelationModel.getWordRelated().getWord());
    }

    /**
     * Mappea desde una lista de WordRelationModels y genera una lista de <Strings> que son palabras sinónimas.
     * @param wordRelationModelList
     * @return
     */
    public List<String> mapSynonyms(List<WordRelationModel> wordRelationModelList){
        if(wordRelationModelList == null) return List.of(); //Return lista vacia
        return wordRelationModelList.stream()
                .filter(rel -> rel.getRelationEnumType() == RelationEnumType.SINONIMA)
                .map(rel -> rel.getWordRelated().getWord())
                .collect(Collectors.toList());
    }

    /**
     * Mapea desde una lista de WordRelationModels y genera una lista de <Strings> que son palabras antónimas.
     * @param wordRelationModelList
     * @return
     */
    public List<String> mapAntonyms(List<WordRelationModel> wordRelationModelList){
        if(wordRelationModelList == null) return List.of();
        return wordRelationModelList.stream()
                .filter(rel -> rel.getRelationEnumType() == RelationEnumType.ANTONIMA)
                .map(rel -> rel.getWordRelated().getWord())
                .collect(Collectors.toList());
    }
}
