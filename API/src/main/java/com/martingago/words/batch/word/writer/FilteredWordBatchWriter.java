package com.martingago.words.batch.word.writer;

import com.martingago.words.batch.model.WordBatch;
import com.martingago.words.batch.repository.word.WordBatchRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class FilteredWordBatchWriter implements ItemWriter<WordBatch> {

    private final JpaItemWriter<WordBatch> jpaItemWriter;

    /**
     * Recibe un chunk de WordBatch, realiza un filtrado previo comprobando las entidades previamente existentes.
     * @param chunk
     * @throws Exception
     */
    @Override
    public void write(Chunk<? extends WordBatch> chunk) throws Exception {
        System.out.println("Palabras a añadir/actualizar: " + chunk.size());
        log.info("Palabras a añadir/actualizar: {}",chunk.size());
        if (!chunk.isEmpty()) {
            jpaItemWriter.write(chunk);
        }
    }
}

