package com.martingago.words.dto.global.batch;

import java.time.LocalDateTime;
import java.util.List;

public record JobStatsDTO(
        String jobName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationMillis,
        List<StepStatsDTO> steps
) {}



