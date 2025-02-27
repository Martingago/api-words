package com.martingago.words.dto.global;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordStaticsDTO {

    private Long wordsCount;
    private Long wordsDefinitionsCount;
    private Long wordsExamplesCount;
    private Long wordsSynonymsCount;
}
