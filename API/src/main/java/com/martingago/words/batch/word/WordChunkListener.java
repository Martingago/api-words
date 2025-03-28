package com.martingago.words.batch.word;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;
import java.util.List;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WordChunkListener implements ChunkListener {

    private final WordBatchRepository wordBatchRepository;
    private Map<String, WordBatch> existingWordMap;



    @Override
    public void afterChunk(ChunkContext chunkContext) {
        existingWordMap = null; // Limpiar después del chunk
    }

    public Map<String, WordBatch> getExistingWordMap() {
        return existingWordMap;
    }
}