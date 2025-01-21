package com.martingago.words.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDefinitionDTO {

    private String qualification;
    private String definition;
    private Set<String> examples;
    private Set<String> synonyms;
    private Set<String> antonyms;
}
