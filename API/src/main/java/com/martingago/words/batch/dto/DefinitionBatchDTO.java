package com.martingago.words.batch.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DefinitionBatchDTO {
    private String qualification;
    private String definition;
    private Set<String> synonyms;
    private Set<String> antonyms;
    private List<String> examples;
}
