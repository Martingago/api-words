package com.martingago.words.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Convierte la entrada de un fichero json a un map
     * @param file
     * @return
     * @throws IOException
     */
    public Map<String, WordResponseDTO> parseJsonFileToWordMap(MultipartFile file) throws IOException {
        // Leer el JSON y convertirlo a un conjunto de WordResponseDTO
        Set<WordResponseDTO> wordSet = objectMapper.readValue(
                file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(Set.class, WordResponseDTO.class)
        );
        // Convertir el Set a un Map, usando la propiedad getWord() como clave
        return wordSet.stream().collect(Collectors.toMap(WordResponseDTO::getWord, word -> word));
    }
}
