package com.martingago.words.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;

import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Iterator;
import java.util.List;

public class WordItemReader implements ItemReader<WordResponseDTO> {

    private ObjectMapper objectMapper;
    private Iterator<WordResponseDTO> iterator;

    /**
     * Constructor that loads and parses the JSON file.
     */
    public WordItemReader(String filePath) {
        this.objectMapper = new ObjectMapper();  // Jackson ObjectMapper to read JSON
        try{
            Resource resource = new ClassPathResource(filePath);
            List<WordResponseDTO> wordList = objectMapper.readValue(resource.getInputStream(), objectMapper.getTypeFactory().constructCollectionType(List.class, WordResponseDTO.class));
            this.iterator = wordList.iterator();  // Create an iterator to read the words one by one
        }catch (Exception e){
            System.out.println("error handling file: " + e.getMessage());
        }

    }


    /**
     * Read the next WordResponseDTO from the JSON file.
     */
    @Override
    public WordResponseDTO read() throws Exception {
        if (iterator.hasNext()) {
            return iterator.next();  // Return the next word DTO
        }
        return null;  // Return null when there are no more elements to read
    }
}
