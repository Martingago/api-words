package com.martingago.words.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WordItemWriter implements ItemWriter<WordBatch> {

    @Autowired
    WordBatchRepository wordBatchRepository;


    @Override
    public void write(Chunk<? extends WordBatch> chunk) throws Exception {
        wordBatchRepository.saveAll((List<WordBatch>) chunk);
    }
}
