package com.martingago.words.dto.global.batch;

import io.swagger.v3.oas.annotations.media.Schema;

public record StepStatsDTO(
        @Schema(description = "Nombre del step realizado")
        String stepName,

        @Schema(description = "Número de elementos leídos en la ejecución del step")
        long readCount,

        @Schema(description = "Número de elementos escritos en la ejecución del step")
        long writeCount,

        @Schema(description = "Número de elementos saltados en la ejecución del step")
        long skipCount,

        @Schema(description = "Estado de ejecución del step")
        String status
) {}
