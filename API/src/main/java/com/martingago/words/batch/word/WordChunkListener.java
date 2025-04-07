package com.martingago.words.batch.word;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WordChunkListener implements ChunkListener {

    /**
     * Tras ejecutarse cada chunk, se limpian los datos del contexto para evitar sobresaturar la memoria local.
     * @param context
     */
    @Override
    public void afterChunk(ChunkContext context) {
        ExecutionContext executionContext = context.getStepContext()
                .getStepExecution().getExecutionContext();
        executionContext.remove("wordBatchMap");
        executionContext.remove("newWordsToPersistMap");
    }

}