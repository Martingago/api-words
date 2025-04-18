package com.martingago.words.dto.global.batch;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record JobStatsDTO(
        @Schema(description = "Nombre del job realizado")
        String jobName,

        @Schema(description = "Estado del job")
        String status,

        @Schema(description = "Time milliseconds en el que se inició el job")
        LocalDateTime startTime,

        @Schema(description = "Time milliseconds en el que finaliza el job")
        LocalDateTime endTime,

        @Schema(description = "Time milliseconds duración ejecución del job")
        long durationMillis,

        @Schema(description = "Listado de steps realizados durante el job")
        List<StepStatsDTO> steps
) {}



