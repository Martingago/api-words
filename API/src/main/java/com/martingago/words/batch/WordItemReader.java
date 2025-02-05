package com.martingago.words.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;;
import java.util.Iterator;
import java.util.List;

public class WordItemReader implements ItemReader<WordResponseDTO> {

    private Iterator<WordResponseDTO> iterator;

    public WordItemReader() {
            initializeReader();
    }

    /**
     * Lee el fichero recibido por el usuario y lo transforma en un obejeto comprensible por la aplicación.
     */
    private void initializeReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Resource resource = new ClassPathResource("palabras_definiciones_1.json");
            List<WordResponseDTO> wordList = objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WordResponseDTO.class)
            );

            this.iterator = wordList.iterator();
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    @Override
    public WordResponseDTO read() {
        return iterator != null && iterator.hasNext() ? iterator.next() : null;
    }


}