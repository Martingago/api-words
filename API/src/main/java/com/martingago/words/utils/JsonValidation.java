package com.martingago.words.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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
        // Verifica el tipo MIME y la extensión del archivo
        boolean isJson = contentType != null && contentType.equals("application/json")
                || (filename != null && filename.endsWith(".json"));

        boolean isJsonl = filename != null && filename.endsWith(".jsonl");

        return isJsonl || isJson;
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

    /**
     * Función que recibe un fichero jsonl y lo mapea a un map de WordResponseDTO
     * @param file
     * @return
     * @throws IOException
     */
    public Map<String, WordResponseDTO> parseJsonlFileToWordMap(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, WordResponseDTO> wordMap;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            wordMap = reader.lines()
                    .map(line -> {
                        try {
                            return objectMapper.readValue(line, WordResponseDTO.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Error parsing JSON line: " + line, e);
                        }
                    })
                    .collect(Collectors.toMap(
                            WordResponseDTO::getWord, // Key
                            word -> word,            // Value
                            (existing, replacement) -> replacement // Merge function: Sobrescribe el valor existente
                    ));
        }

        return wordMap;
    }

    /**
     * Funciñon que recibe un fichero, lo valida, y lo convierte en un map para poder ser procesado por la aplicación.
     * @param file
     * @return
     * @throws IOException
     */
    public Map<String, WordResponseDTO> parseFileToWordMap(MultipartFile file) throws IOException {
        // Validar si el archivo es JSON o JSONL
        if (!isValidJsonFile(file)) {
            throw new IllegalArgumentException("The file is not a valid JSON or JSONL file.");
        }

        // Obtener el nombre del archivo para determinar su tipo
        String filename = file.getOriginalFilename();

        // Procesar el archivo según su tipo
        if (filename != null && filename.endsWith(".json")) {
            return parseJsonFileToWordMap(file); // Procesar como JSON
        } else if (filename != null && filename.endsWith(".jsonl")) {
            return parseJsonlFileToWordMap(file); // Procesar como JSONL
        } else {
            throw new IllegalArgumentException("The file type could not be determined.");
        }
    }


    /**
     * Genera un mensaje de salida de ficheros procesados con éxito/errores
     * @param processedFiles nombre de los ficheros procesados con éxito
     * @param failedFiles nombre de los ficheros que han tenido un error
     * @return
     */
    public String buildResultMessage(List<String> processedFiles, List<String> failedFiles) {
        StringBuilder message = new StringBuilder();

        if (!processedFiles.isEmpty()) {
            message.append("Successfully processed files: ")
                    .append(String.join(", ", processedFiles));
        }

        if (!failedFiles.isEmpty()) {
            if (message.length() > 0) {
                message.append(". ");
            }
            message.append("Failed to process files: ")
                    .append(String.join(", ", failedFiles));
        }

        return message.toString();
    }

}
