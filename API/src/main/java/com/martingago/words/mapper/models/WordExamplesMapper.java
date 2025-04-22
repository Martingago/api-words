package com.martingago.words.mapper.models;

import com.martingago.words.dto.models.example.WordExampleDTO;
import com.martingago.words.domain.model.WordExampleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WordExamplesMapper {

    /**
     * Mapea una lista de WordExampleModel y lo convierte en una lista de Strings
     * @param wordExampleModelSet
     * @return
     */
    public Set<String> toStringSet(Set<WordExampleModel> wordExampleModelSet){
        if(wordExampleModelSet == null) return  Set.of();
        return wordExampleModelSet.
                stream().map(WordExampleModel::getExample)
                .collect(Collectors.toSet());
    }

}
