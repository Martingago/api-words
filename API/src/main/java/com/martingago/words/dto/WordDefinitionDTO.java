package com.martingago.words.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDefinitionDTO {

    private String definition;
    private List<WordExampleDTO> examples;
    private List<String> synonyms;
    private List<String> antonyms;
    private WordQualificationDTO qualification;
}
