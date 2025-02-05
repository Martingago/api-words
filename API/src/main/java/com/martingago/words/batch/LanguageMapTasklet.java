package com.martingago.words.batch;

import com.martingago.words.model.LanguageModel;
import com.martingago.words.service.language.LanguageService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class LanguageMapTasklet implements Tasklet {

    @Autowired
    private LanguageService languageService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // Obtener el mapa de idiomas desde el servicio
        Map<String, LanguageModel> languageMap = languageService.getAllLanguagesMappedByLangCode();

        // Obtener el ExecutionContext para almacenar el mapa de idiomas
        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        // Almacenar el mapa de idiomas en el contexto de ejecución
        executionContext.put("languagesMap", languageMap);

        // Confirmar que la tarea ha finalizado correctamente
        return RepeatStatus.FINISHED;
    }
}

