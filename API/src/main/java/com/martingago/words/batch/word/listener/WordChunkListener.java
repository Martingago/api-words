package com.martingago.words.batch.word.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import java.lang.reflect.Field;

@Slf4j
public class WordChunkListener implements ChunkListener {

    @Override
    public void afterChunk(ChunkContext context) {
        ItemProcessor<?, ?> processor = (ItemProcessor<?, ?>) context.getStepContext()
                .getStepExecution()
                .getExecutionContext()
                .get("processor");

        if (processor != null) {
            resetChunkWordMap(processor);
        }
    }

    private void resetChunkWordMap(ItemProcessor<?, ?> processor) {
        try {
            Field field = processor.getClass().getDeclaredField("chunkWordMap");
            field.setAccessible(true);
            field.set(processor, null);
        } catch (NoSuchFieldException e) {
            log.error("No se encontró el campo chunkWordMap");
            // Log: "No se encontró el campo chunkWordMap", si tienes un sistema de logging
        } catch (IllegalAccessException e) {
            log.error("No se pudo acceder al campo chunkWordMap");
        }
    }
}
