package com.martingago.words.batch.dto;

import lombok.Data;

import java.util.List;

@Data
public class DefinitionBatchDTO {
    private String qualification;
    private String definition;
    private List<String> synonyms;
    private List<String> antonyms;
    private List<String> examples;
}
