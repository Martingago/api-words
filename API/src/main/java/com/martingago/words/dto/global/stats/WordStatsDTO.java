package com.martingago.words.dto.global.stats;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto que contiene la información de las estadísticas principales del API de WordRadar.")
public class WordStatsDTO {

    @Schema(description = "Número total de palabras que existen en la Base de datos de WordRadar.")
    private Long wordsCount;

    @Schema(description = "Número total de clasificaciones individuales existentes en la Base de datos de WordRadar.")
    private Long wordsQualificationCount;

    @Schema(description = "Número total de definiciones que existen en la Base de datos de WordRadar.")
    private Long wordsDefinitionsCount;

    @Schema(description = "Número total de ejemplos que existen en la Base de datos de WordRadar.")
    private Long wordsExamplesCount;

    @Schema(description = "Número total de sinónimos que existen en la Base de datos de WordRadar.")
    private Long wordsSynonymsCount;

    @Schema(description = "Número total de antónimos que existen en la Base de datos de WordRadar.")
    private Long wordsAntonymsCount;
}
