package com.martingago.words.POJO;

import com.martingago.words.model.WordDefinitionModel;
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
