package com.martingago.words.batch.word.listener;

import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordChunkListener implements ChunkListener {
    private final WordBatchRepository wordBatchRepository;

    // Claves para el ExecutionContext
    private static final String EXISTING_WORDS_KEY = "existingWords";
    private static final String CHUNK_WORDS_KEY = "chunkWords";

    @Override
    public void beforeChunk(ChunkContext context) {
        // Limpiar datos del chunk anterior
        ExecutionContext stepContext = context.getStepContext()
                .getStepExecution()
                .getExecutionContext();

        // Inicializar estructuras para el nuevo chunk
        stepContext.put(EXISTING_WORDS_KEY, new HashMap<String, WordBatch>());
        stepContext.put(CHUNK_WORDS_KEY, new HashSet<String>());
        log.info("Inicio de nuevo chunk. Datos limpiados");
    }


    @Override
    public void afterChunk(ChunkContext context) {
        // Obtener las palabras recolectadas durante la lectura/procesamiento
        ExecutionContext stepContext = context.getStepContext()
                .getStepExecution()
                .getExecutionContext();

        @SuppressWarnings("unchecked")
        Set<String> chunkWords = (Set<String>) stepContext.get(CHUNK_WORDS_KEY);

        if (chunkWords != null && !chunkWords.isEmpty()) {
            // Consultar palabras existentes en la base de datos en una sola query
            Set<WordBatch> existingWords = wordBatchRepository.findByWordIn(chunkWords);

            @SuppressWarnings("unchecked")
            Map<String, WordBatch> existingWordsMap = (Map<String, WordBatch>) stepContext.get(EXISTING_WORDS_KEY);

            existingWords.forEach(word -> existingWordsMap.put(word.getWord(), word));
            stepContext.put(EXISTING_WORDS_KEY, existingWordsMap);

            log.info("Encontradas {} palabras existentes de {} procesadas en el chunk",
                    existingWords.size(), chunkWords.size());
        }
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Error procesando chunk: {}", context.getStepContext().getStepExecution().getFailureExceptions());
        // Podrías limpiar aquí también si quieres manejar errores de forma específica
    }

    // Método auxiliar para que otros componentes (como un ItemReadListener) añadan palabras
    public void addWordToCheck(String word, ChunkContext context) {
        @SuppressWarnings("unchecked")
        Set<String> chunkWords = (Set<String>) context.getStepContext()
                .getStepExecution()
                .getExecutionContext()
                .get(CHUNK_WORDS_KEY);

        if (chunkWords != null) {
            chunkWords.add(word);
        }
    }

}
