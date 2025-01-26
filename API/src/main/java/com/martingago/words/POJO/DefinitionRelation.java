package com.martingago.words.POJO;

import com.martingago.words.model.RelationEnumType;
import com.martingago.words.model.WordDefinitionModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 * Clase que se encarga de gestionar la definición de una palabra y el tipo de relación que tiene.
 * Esta clase se emplea en un mapper dónde la key es una palabra, y los datos a almacenar es un DefinitionRelation
 */
public class DefinitionRelation {
    private WordDefinitionModel wordDefinitionModel;
    private RelationEnumType relationEnumType;
}
