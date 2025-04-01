package com.martingago.words.batch.word.reader;

import com.martingago.words.batch.dto.WordBatchDTO;
import com.martingago.words.batch.dto.WordBatchReferenceDTO;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@StepScope
@RequiredArgsConstructor
public class CustomChunkItemReader implements ItemStreamReader<WordBatchDTO> {

    private final FlatFileItemReader<WordBatchDTO> delegate;
    private final WordBatchRepository wordBatchRepository;

    private final List<WordBatchDTO> chunkBuffer = new ArrayList<>();
    private final Set<String> wordsToFetch = new HashSet<>();
    private Iterator<WordBatchDTO> currentChunkIterator;

    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        delegate.open(executionContext);
    }

    @Override
    public WordBatchDTO read() throws Exception {
        // Si aún quedan elementos en el chunk, retornamos el siguiente
        if (currentChunkIterator != null && currentChunkIterator.hasNext()) {
            return currentChunkIterator.next();
        }

        // Limpiamos el buffer y la lista de palabras antes de procesar un nuevo batch
        chunkBuffer.clear();
        wordsToFetch.clear();

        // Cargamos hasta 100 elementos en el buffer
        WordBatchDTO item;
        while (chunkBuffer.size() < 100 && (item = delegate.read()) != null) {
            chunkBuffer.add(item);

            // Extraemos las palabras, sinónimos y antónimos
            wordsToFetch.add(item.getWord());
            item.getDefinitions().forEach(definition -> {
                if (definition.getSynonyms() != null) wordsToFetch.addAll(definition.getSynonyms());
                if (definition.getAntonyms() != null) wordsToFetch.addAll(definition.getAntonyms());
            });
        }

        // Si el buffer está vacío, terminamos la lectura
        if (chunkBuffer.isEmpty()) {
            return null;
        }

        // Consultamos la base de datos solo al final del batch
        if (!wordsToFetch.isEmpty()) {
            List<WordBatchReferenceDTO> existingBatchRefs = wordBatchRepository.findReferencesByWordIn(wordsToFetch);

            Map<String, WordBatchReferenceDTO> wordReferenceMap = existingBatchRefs.stream()
                    .collect(Collectors.toMap(WordBatchReferenceDTO::getWord, ref -> ref));

            // Guardamos las referencias en el ExecutionContext
            executionContext.put("wordBatchMap", wordReferenceMap);
        }

        // Reiniciamos el iterador del buffer
        currentChunkIterator = chunkBuffer.iterator();
        return currentChunkIterator.next();
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
