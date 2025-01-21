package com.martingago.words.mapper;

import com.martingago.words.dto.WordExampleDTO;
import com.martingago.words.model.WordExampleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WordExamplesMapper {

    /**
     * Genera un WordExampleDTO a partir del WordExampleModel
     * @param wordExampleModel
     * @return
     */
    public WordExampleDTO toDTO(WordExampleModel wordExampleModel){
        if(wordExampleModel == null) return null;
        return new WordExampleDTO(
                wordExampleModel.getExample()
        );
    }

    /**
     * Mapea una lista de WordExampleModel y lo convierte en una lista de WordExampleDTO (Strings)
     * @param wordExampleModelSet
     * @return
     */
    public Set<WordExampleDTO> toDTOSet(Set<WordExampleModel> wordExampleModelSet){
        if(wordExampleModelSet == null) return Set.of();
        return wordExampleModelSet.
                stream().map(this::toDTO)
                .collect(Collectors.toSet());
    }

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

    /**
     * Genera un WordExampleModel desde un WordExampleDTO simple
     * @param wordExampleDTO
     * @return
     */
    public WordExampleModel toEntity(WordExampleDTO wordExampleDTO){
        return WordExampleModel.builder()
                .example(wordExampleDTO.getExample())
                .build();
    }
}
