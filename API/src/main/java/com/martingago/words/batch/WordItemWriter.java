package com.martingago.words.batch;

import com.martingago.words.model.WordModel;
import com.martingago.words.repository.WordRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class WordItemWriter implements ItemWriter<WordModel> {

    @Autowired
    WordRepository wordRepository;


    @Override
    public void write(Chunk<? extends WordModel> chunk) throws Exception {
        wordRepository.saveAll(chunk.getItems());
    }
}
