package com.martingago.words.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto que contiene la clasificación de una palabra.")
public class WordQualificationDTO {

    @Schema(description = "Clasificación de la palabra.")
    private String qualification;
}
