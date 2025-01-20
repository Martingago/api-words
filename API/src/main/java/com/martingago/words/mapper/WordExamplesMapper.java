package com.martingago.words.mapper;

import com.martingago.words.dto.WordExampleDTO;
import com.martingago.words.model.WordExampleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordExamplesMapper {

    /**
     * Genera un WordExampleDTO a partir del WordExampleModel
     * @param wordExampleModel
     * @return
     */
    public WordExampleDTO toDTO(WordExampleModel wordExampleModel){
        return new WordExampleDTO(
                wordExampleModel.getExample()
        );
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
