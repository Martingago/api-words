package com.martingago.words.batch.word;

import com.martingago.words.batch.dto.WordBatchDTO;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.HashSet;
import java.util.Set;

public class ChunkCollectingItemReader implements ItemStreamReader<WordBatchDTO> {

    private final FlatFileItemReader<WordBatchDTO> delegate;
    private Set<String> chunkData = new HashSet<>();

    public ChunkCollectingItemReader(FlatFileItemReader<WordBatchDTO> delegate) {
        this.delegate = delegate;
    }

    @Override
    public WordBatchDTO read() throws Exception {
        WordBatchDTO item = delegate.read();
        if (item != null) {
            chunkData.add(item.toString()); // Guardar en el Set
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("iniciando lectura de chunk");
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // Guardar datos del chunk antes de procesar
        executionContext.put("CHUNK_DATA", new HashSet<>(chunkData));
        chunkData.clear();

        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).update(executionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("fin lectura de chunk");
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).close();
        }
    }
}