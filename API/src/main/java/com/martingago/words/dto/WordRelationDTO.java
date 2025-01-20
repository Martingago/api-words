package com.martingago.words.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordRelationDTO {
    private Long relatedWordId;
    private String relationType;
}
