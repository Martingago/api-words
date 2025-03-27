package com.martingago.words.batch.word.listener;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WordChunkListener implements ChunkListener {


    private final WordBatchRepository wordBatchRepository;

    @Override
    public void beforeChunk(ChunkContext chunkContext) {
        var stepExecution = chunkContext.getStepContext().getStepExecution();
        var executionContext = stepExecution.getExecutionContext();

        @SuppressWarnings("unchecked")
        List<WordBatchDTO> chunkItems = (List<WordBatchDTO>) executionContext.get("chunkItems");
        System.out.println(chunkItems.size() + "tamaño de los chunkItems");
        if (chunkItems != null && !chunkItems.isEmpty()) {
            Set<String> chunkWords = chunkItems.stream()
                    .map(WordBatchDTO::getWord)
                    .collect(Collectors.toSet());

            Set<WordBatch> existingWords = wordBatchRepository.findByWordIn(chunkWords);
            Set<String> existingWordSet = existingWords.stream()
                    .map(WordBatch::getWord)
                    .collect(Collectors.toSet());

            executionContext.put("existingWords", existingWordSet);
        } else {
            executionContext.put("existingWords", new HashSet<String>());
        }
    }

    @Override
    public void afterChunk(ChunkContext chunkContext) {
        // Limpiar la lista de items para el siguiente chunk
        var executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        executionContext.put("chunkItems", new ArrayList<WordBatchDTO>()); // Reinicia la lista
    }

}