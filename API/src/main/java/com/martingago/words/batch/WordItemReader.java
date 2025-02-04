package com.martingago.words.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

public class WordItemReader implements ItemReader<WordResponseDTO>, ResourceAwareItemReaderItemStream<WordResponseDTO> {

    private Iterator<WordResponseDTO> iterator;
    private Resource resource;

    public WordItemReader() {}

    public WordItemReader(File jsonFile) {
        try {
            this.resource = new UrlResource(jsonFile.toURI());
            initializeReader();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error creating resource from file", e);
        }
    }

    private void initializeReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<WordResponseDTO> wordList = objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, WordResponseDTO.class));
            this.iterator = wordList.iterator();
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    @Override
    public WordResponseDTO read() {
        return iterator != null && iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
        initializeReader();
    }

    @Override
    public void close() {}

}