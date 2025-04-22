package com.martingago.words.batch.word.reader;

import com.martingago.words.dto.models.word.SimpleWordSerializableDTO;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.repository.models.WordRepository;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.*;
import java.util.stream.Collectors;

@StepScope
@RequiredArgsConstructor
public class CustomChunkItemReader implements ItemStreamReader<WordDTO> {

    private final FlatFileItemReader<WordDTO> delegate;
    private final WordRepository wordRepository;
    private final WordMapper wordMapper;

    private List<WordDTO> chunkBuffer = new ArrayList<>();
    private Set<String> wordsToFetch = new HashSet<>();
    private Map<String, WordModel> newWordsToPersist = new HashMap<>();
    private Iterator<WordDTO> currentChunkIterator;

    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        delegate.open(executionContext);
    }

    @Override
    public WordDTO read() throws Exception {
        // Si aún quedan elementos en el chunk, retornamos el siguiente
        if (currentChunkIterator != null && currentChunkIterator.hasNext()) {
            return currentChunkIterator.next();
        }

        // Limpiamos el buffer y la lista de palabras antes de procesar un nuevo batch
        chunkBuffer.clear();
        wordsToFetch.clear();

        // Cargamos hasta 100 elementos en el buffer
        WordDTO item;


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

            Set<WordModel> existingWordModelRefs = wordRepository.findWordModelIn(wordsToFetch);

            Map<String, SimpleWordSerializableDTO> wordBatchReferenceDTOMap = existingWordModelRefs.stream()
                    .collect(Collectors.toMap(
                            WordModel::getWord,
                            wordMapper::toWordBatchReferenceDTO
                    ));

            // Guardamos las referencias en el ExecutionContext
            executionContext.put("wordBatchMap", wordBatchReferenceDTOMap);
            //Genera el map de palabras a persistir limpio
            executionContext.put("newWordsToPersistMap", newWordsToPersist);
        }

        // Reiniciamos el iterador del buffer
        currentChunkIterator = chunkBuffer.iterator();
        return currentChunkIterator.next();
    }

}
