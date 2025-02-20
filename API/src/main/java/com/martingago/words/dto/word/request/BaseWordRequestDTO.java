package com.martingago.words.dto.word.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true,
        defaultImpl = FullWordRequestDTO.class //Valor por defecto si no se envía un type
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelatedWordRequestDTO.class, name = "related"),
        @JsonSubTypes.Type(value = FullWordRequestDTO.class, name = "full")
})

/**
 * Clase abstracta principal de la que extienden la FullWordRequestDTO y la RelatedWordRequestDTO.
 * Esta clase maneja los tipos de datos que se reciben desde el micro-servicio de procesamiento convirtiéndolos
 * en un objeto heredado de esta clase principal.
 */
public abstract class BaseWordRequestDTO {
    private String type;
}
