package com.martingago.words.dto.models.word;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor

/**
 * DTO personalizado para la eliminación de palabras de la base de datos.
 */
@Schema(description = "Objeto utilizado para eliminar palabras correctamente de la base de datos asociadas a un idioma.")
public class DeleteWordRequestDTO {
    @NonNull
    @Schema(description = "String con el código de idioma de la palabra")
    private String langCode;
    @NonNull
    @Schema(description = "String con el texto de la palabra a eliminar")
    private String word;
}
