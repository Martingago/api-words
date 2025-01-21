package com.martingago.words.mapper;

import com.martingago.words.dto.WordRelationDTO;
import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordRelationModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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
     * @param wordRelationModelSet
     * @return
     */
    public Set<String> mapSynonyms(Set<WordRelationModel> wordRelationModelSet){
        if(wordRelationModelSet == null) return Set.of(); //Return lista vacia
        return wordRelationModelSet.stream()
                .filter(rel -> rel.getRelationEnumType() == RelationEnumType.SINONIMA)
                .map(rel -> rel.getWordRelated().getWord())
                .collect(Collectors.toSet());
    }

    /**
     * Mapea desde una lista de WordRelationModels y genera una lista de <Strings> que son palabras antónimas.
     * @param wordRelationModelSet
     * @return
     */
    public Set<String> mapAntonyms(Set<WordRelationModel> wordRelationModelSet){
        if(wordRelationModelSet == null) return Set.of();
        return wordRelationModelSet.stream()
                .filter(rel -> rel.getRelationEnumType() == RelationEnumType.ANTONIMA)
                .map(rel -> rel.getWordRelated().getWord())
                .collect(Collectors.toSet());
    }
}
