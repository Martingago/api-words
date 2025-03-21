package com.martingago.words.batch.word.listener;

import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import com.martingago.words.model.LanguageModel;
import com.martingago.words.model.WordQualificationModel;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.Map;

public class WordBatchStepListener implements StepExecutionListener {

    private final WordBatchProcessor processor;
    private final Map<String, LanguageModel> languageMap;
    private final Map<String, WordQualificationModel> qualificationMap;

    public WordBatchStepListener(
            WordBatchProcessor processor,
            Map<String, LanguageModel> languageMap,
            Map<String, WordQualificationModel> qualificationMap) {
        this.processor = processor;
        this.languageMap = languageMap;
        this.qualificationMap = qualificationMap;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Inicializar recursos antes del step
        processor.setLanguageMap(languageMap);
        processor.setQualificationMap(qualificationMap);
        processor.clearChunkWordMap(); // Inicializar el mapa limpio
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Limpiar recursos después del step
        processor.clearChunkWordMap();
        return stepExecution.getExitStatus();
    }
}
