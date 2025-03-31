package com.martingago.words.batch.word.reader;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@StepScope
@RequiredArgsConstructor
public class CustomChunkItemReader implements ItemStreamReader<WordBatchDTO> {

    private final FlatFileItemReader<WordBatchDTO> delegate;
    private final WordBatchRepository wordBatchRepository;

    private final List<WordBatchDTO> chunkBuffer = new ArrayList<>();
    private Iterator<WordBatchDTO> currentChunkIterator;

    // Guardamos el ExecutionContext para usarlo en read()
    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        delegate.open(executionContext);
    }

    @Override
    public WordBatchDTO read() throws Exception {
        // Si aún tenemos ítems en el buffer, devolvemos uno
        if (currentChunkIterator != null && currentChunkIterator.hasNext()) {
            return currentChunkIterator.next();
        }

        // Si estamos procesando un nuevo chunk, llenamos el buffer
        chunkBuffer.clear();
        WordBatchDTO item;
        while (chunkBuffer.size() < 100 && (item = delegate.read()) != null) {
            chunkBuffer.add(item);
        }

        // Si el buffer no está vacío, consultamos en bloque la BBDD
        if (!chunkBuffer.isEmpty()) {
            // Obtenemos las palabras del chunk
            List<String> words = chunkBuffer.stream()
                    .map(WordBatchDTO::getWord)
                    .collect(Collectors.toList());

            // Obtenemos de la BBDD los WordBatch que ya existen para estas palabras
            Set<WordBatch> existingBatchSet = wordBatchRepository.findByWordIn(new java.util.HashSet<>(words));
            Map<String, WordBatch> wordBatchMap = existingBatchSet.stream()
                    .collect(Collectors.toMap(WordBatch::getWord, wb -> wb));

            System.out.println("Referencias añadidas en el ExecutionContext: " + wordBatchMap.size());
            // Guardamos el mapa en el ExecutionContext bajo una clave definida, por ejemplo "wordBatchMap"
            executionContext.put("wordBatchMap", wordBatchMap);

            // Preparamos el iterador para servir ítems desde el buffer
            currentChunkIterator = chunkBuffer.iterator();
            return currentChunkIterator.next();
        }

        // Retornamos null cuando ya no haya más datos
        return null;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
