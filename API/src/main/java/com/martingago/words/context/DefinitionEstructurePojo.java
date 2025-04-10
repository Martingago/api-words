package com.martingago.words.context;

import com.martingago.words.domain.model.WordDefinitionModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class DefinitionEstructurePojo {
    private WordDefinitionModel wordDefinitionModel;
    private List<String> listExamples;
    private List<WordRelationPojo> relationPojoList; //Lista de relaciones con otras palabras.

}
