package com.martingago.words.dto.global.batch;

public record StepStatsDTO(
        String stepName,
        long readCount,
        long writeCount,
        long skipCount,
        String status
) {}
