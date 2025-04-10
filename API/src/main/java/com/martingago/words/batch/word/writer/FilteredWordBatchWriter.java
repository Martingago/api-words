package com.martingago.words.batch.word.writer;

import com.martingago.words.domain.model.WordModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class FilteredWordBatchWriter implements ItemWriter<WordModel> {

    private final JpaItemWriter<WordModel> jpaItemWriter;

    /**
     * Recibe un chunk de WordBatch, realiza un filtrado previo comprobando las entidades previamente existentes.
     * @param chunk
     * @throws Exception
     */
    @Override
    public void write(Chunk<? extends WordModel> chunk) throws Exception {
        System.out.println("Palabras a escribir: " + chunk.size());
        if (!chunk.isEmpty()) {
            jpaItemWriter.write(chunk);
        }
    }
}

