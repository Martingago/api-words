package com.martingago.words.dto.microservices.word.external;

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
        defaultImpl = WordDTOExternal.class //Valor por defecto si no se envía un type
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelatedWordDTOExternal.class, name = "related"),
        @JsonSubTypes.Type(value = WordDTOExternal.class, name = "full")
})

/**
 * Clase abstracta base para representar los diferentes tipos de objetos de palabras
 * recibidos desde microservicios externos. Esta clase permite la deserialización polimórfica
 * en función del atributo 'type', diferenciando entre los distintos tipos de palabras que
 * puede devolver el microservicio.
 *
 * Subtipos:
 * - {@link RelatedWordDTOExternal} para palabras relacionadas.
 * - {@link WordDTOExternal} para palabras completas.
 */

public abstract class ExternalBaseWordDTO {
    private String type;
}
