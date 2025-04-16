package com.martingago.words.dto.microservices.word.external;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter

/**
 * DTO que representa una palabra relacionada recibida desde un microservicio.
 * Este objeto se utiliza cuando el microservicio no devuelve una palabra procesada
 * directamente, sino una palabra asociada (por ejemplo, una variante ortográfica,
 * plural o forma femenina de otra palabra).
 */

@Schema(description = "Palabra relacionada con la palabra objetivo (Falta ortográfica, plural, femenino...)")
public class RelatedWordDTOExternal extends ExternalBaseWordDTO {

    @NonNull
    @Schema(description = "Palabra relacionada devuelta por el microservicio")
    private String relatedWord;
}
