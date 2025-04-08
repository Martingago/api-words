package com.martingago.words.batch.word;

import com.martingago.words.dto.word.request.WordBatchReferenceDTO;
import com.martingago.words.model.WordModel;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class WordProcessingStepListener implements StepExecutionListener {


    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getExecutionContext();
        context.put("wordBatchMap", new HashMap<String, WordBatchReferenceDTO>());
        context.put("newWordsToPersistMap", new HashMap<String, WordModel>());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Al final de todo el step, puedes hacer limpieza más grande si quieres.
        return ExitStatus.COMPLETED;
    }
}

