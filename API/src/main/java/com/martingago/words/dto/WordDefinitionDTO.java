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
    private List<WordRelationDTO> synonyms;
    private List<WordRelationDTO> antonyms;
    private WordQualificationDTO qualification;
}
