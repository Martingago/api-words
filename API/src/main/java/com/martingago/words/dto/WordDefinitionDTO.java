package com.martingago.words.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDefinitionDTO {

    private String qualification;
    private String definition;
    private List<String> examples;
    private List<String> synonyms;
    private List<String> antonyms;
}
