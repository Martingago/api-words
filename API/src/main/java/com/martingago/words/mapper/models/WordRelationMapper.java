package com.martingago.words.mapper.models;

import com.martingago.words.dto.models.relation.WordRelationDTO;
import com.martingago.words.domain.model.RelationEnumType;
import com.martingago.words.domain.model.WordRelationModel;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WordRelationMapper {

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
