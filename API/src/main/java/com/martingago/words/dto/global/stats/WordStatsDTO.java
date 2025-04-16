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

    @Schema(description = "Número total de palabras en la base de datos.", example = "45034")
    private Long wordsCount;

    @Schema(description = "Número total de clasificaciones individuales.", example = "82")
    private Long wordsQualificationCount;

    @Schema(description = "Número total de definiciones disponibles.", example = "92784")
    private Long wordsDefinitionsCount;

    @Schema(description = "Número total de ejemplos registrados.", example = "12899")
    private Long wordsExamplesCount;

    @Schema(description = "Número total de sinónimos existentes.", example = "23874")
    private Long wordsSynonymsCount;

    @Schema(description = "Número total de antónimos disponibles.", example = "14789")
    private Long wordsAntonymsCount;
}

