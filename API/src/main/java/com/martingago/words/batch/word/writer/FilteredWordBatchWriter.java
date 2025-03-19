package com.martingago.words.batch.word.writer;

import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FilteredWordBatchWriter implements ItemWriter<WordBatch> {

    private final WordBatchRepository wordBatchRepository;
    private final JpaItemWriter<WordBatch> jpaItemWriter;


    /**
     * Recibe un chunk de WordBatch, realiza un filtrado previo comprobando las entidades previamente existentes.
     * @param chunk
     * @throws Exception
     */
    @Override
    public void write(Chunk<? extends WordBatch> chunk) throws Exception {
        // Extraer las palabras del chunk
        List<String> words = chunk.getItems().stream()
                .map(WordBatch::getWord)
                .collect(Collectors.toList());

        // Consultar palabras existentes con is_placeholder = false
        List<String> existingNonPlaceholderWords = wordBatchRepository.findExistingNonPlaceholderWords(words);

        // Filtrar el chunk: excluir palabras que ya existen y no son placeholders
        List<WordBatch> itemsToWrite = chunk.getItems().stream()
                .filter(item -> !existingNonPlaceholderWords.contains(item.getWord()))
                .collect(Collectors.toList());
        System.out.println("filtradas palabras a añadir: " + itemsToWrite.size() + "/" + chunk.size());
        // Escribir solo las palabras filtradas
        if (!itemsToWrite.isEmpty()) {
            jpaItemWriter.write(new Chunk<>(itemsToWrite));
        }
    }
}

