package com.martingago.words.POJO;

import com.martingago.words.model.RelationEnumType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WordRelationPojo {

    private String word; //String palabra relacionada
    private RelationEnumType relationEnumType; // Tipo de relación con la palabra: SINONIMA / ANTÓNIMA
}
