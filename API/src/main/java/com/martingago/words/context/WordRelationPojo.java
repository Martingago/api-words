package com.martingago.words.context;

import com.martingago.words.domain.model.RelationEnumType;
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
