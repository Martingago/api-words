package com.martingago.words.batch.language.writer;

import com.martingago.words.batch.word.procesor.WordBatchProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;
import com.martingago.words.model.LanguageModel;
import org.springframework.batch.item.ItemWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LanguageWriter implements ItemWriter<LanguageModel>, StepExecutionListener {

    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends LanguageModel> chunk) {
        Map<String, LanguageModel> languageMap = new HashMap<>();
        for (LanguageModel language : chunk) {
            languageMap.put(language.getLangCode(), language);
        }

        // Guardamos en el contexto del Job
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put("languageMap", languageMap);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}

