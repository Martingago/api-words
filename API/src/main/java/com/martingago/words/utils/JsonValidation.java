package com.martingago.words.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Component
public class JsonValidation {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Función que se encarga de validar la estructura de datos de un fichero .json
     * @param file
     * @return
     */
    public boolean isValidJsonFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        return contentType != null && contentType.equals("application/json")
                || (filename != null && filename.endsWith(".json"));
    }

    /**
     * Lee el archivo JSON y lo convierte en un Set<WordResponseDTO> para ser manejado por la aplicación.
     * @param file
     * @return
     * @throws IOException
     */
    public Set<WordResponseDTO> parseJsonFileToWordSet(MultipartFile file) throws IOException {
        return objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(Set.class, WordResponseDTO.class));
    }
}
