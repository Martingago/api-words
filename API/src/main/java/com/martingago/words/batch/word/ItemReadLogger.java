package com.martingago.words.batch.word;

import com.martingago.words.batch.dto.WordBatchDTO;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class ItemReadLogger implements ItemReadListener<WordBatchDTO> {

    @Override
    public void beforeRead() {
        // No necesario
    }

    @Override
    public void afterRead(WordBatchDTO item) {
        System.out.println("ITEM LEÍDO: " + item.getWord());
    }

    @Override
    public void onReadError(Exception ex) {
        System.err.println("Error al leer: " + ex.getMessage());
    }
}