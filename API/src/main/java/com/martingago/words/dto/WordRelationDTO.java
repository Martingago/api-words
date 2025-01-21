package com.martingago.words.dto;

import com.martingago.words.model.RelationEnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordRelationDTO {
    private RelationEnumType relationType; //Tipo de relación con la palabra.
    private String relatedWord; //String de la palabra con la que tiene relación.
}
