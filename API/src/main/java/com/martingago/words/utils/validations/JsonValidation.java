package com.martingago.words.utils.validations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Component
public class JsonValidation {

    /**
     * Función que se encarga de validar la estructura de datos de un fichero .json
     * @param file
     * @return
     */
    public boolean isValidJsonFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        // Verifica el tipo MIME y la extensión del archivo
        return  contentType != null && contentType.equals("application/json")
                || (filename != null && filename.endsWith(".jsonl"));
    }

    /**
     * Comprueba que las líneas del fichero jsonl sean correctas
     * @param file
     * @return
     */
    public boolean isProperJsonlContent(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Intentar parsear cada línea como JSON
                new ObjectMapper().readTree(line);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }


}
