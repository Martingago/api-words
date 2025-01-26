package com.martingago.words.POJO;

import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordDefinitionModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefinitionRelation {
    private WordDefinitionModel wordDefinitionModel;
    private RelationEnumType relationEnumType;
}
