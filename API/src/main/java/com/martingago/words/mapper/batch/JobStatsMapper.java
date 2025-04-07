package com.martingago.words.mapper.batch;

import com.martingago.words.dto.global.batch.JobStatsDTO;
import com.martingago.words.dto.global.batch.StepStatsDTO;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class JobStatsMapper {

    /**
     * Función que transforma los datos de un job (JobExecution) en un objeto JobStatsDTO con las estadísticas del JOB ejecutado
     * @param execution estadísticas que se quieren enviar al front-end
     * @return JobStatsDTO con la información procesada lista para enviar al front-end.
     */
    public JobStatsDTO map(JobExecution execution) {
        execution.getStatus();
        return new JobStatsDTO(
                Optional.of(execution.getJobInstance())
                        .map(JobInstance::getJobName)
                        .orElse("UnknownJob"),
                execution.getStatus().toString(),
                execution.getStartTime(),
                execution.getEndTime(),
                calculateDurationInMillis(execution),
                mapStepExecutions(execution.getStepExecutions().stream().toList())
        );
    }

    /**
     * Calcula el tiempo de ejecución del job; En caso de error devuelve null
     * @param execution objeto JobExecution con la información a extraer.
     * @return Long con el tiempo de ejecución de la tarea.
     */
    private Long calculateDurationInMillis(JobExecution execution) {
        if (execution.getStartTime() != null && execution.getEndTime() != null) {
            return Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis();
        }
        return null;
    }

    /**
     * Extrae la información de los steps que conforman el job.
     * @param stepExecutions Listado de Steps que conforman el job.
     * @return Listado de StepStatsDTO que tienen la información disponible para enviar al front-end.
     */
    private List<StepStatsDTO> mapStepExecutions(List<StepExecution> stepExecutions) {
        return stepExecutions.stream()
                .map(step -> new StepStatsDTO(
                        step.getStepName(),
                        step.getReadCount(),
                        step.getWriteCount(),
                        step.getSkipCount(),
                        step.getStatus() != null ? step.getStatus().toString() : "UNKNOWN"
                ))
                .collect(Collectors.toList());
    }
}
