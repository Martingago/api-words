package com.martingago.words.dto.models.example;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Objeto que contiene un ejemplo de palabra")
public class WordExampleDTO {
    @Schema(description = "Ejemplo de uso de una palabra")
    private String example;
}
