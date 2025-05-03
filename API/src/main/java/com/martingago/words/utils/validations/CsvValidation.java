package com.martingago.words.utils.validations;

import com.martingago.words.exceptions.domain.CustomExceptions;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class CsvValidation {

    /**
     * Lee un archivo CSV de texto plano y devuelve un Set limpio de palabras válidas.
     *
     * @param file Fichero recibido desde el cliente.
     * @return Set con las palabras leídas y normalizadas.
     * @throws IOException si hay problemas leyendo el archivo.
     */
    public Set<String> readWordsFromCsv(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new CustomExceptions.FileEmptyException("El archivo está vacío.");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/csv") && !contentType.equals("text/plain"))) {
            throw new CustomExceptions.UnsupportedFileTypeException("Tipo de archivo no soportado: " + contentType);
        }

        Set<String> words = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int maxWords = 100000;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Saltar líneas vacías o inválidas
                if (line.isEmpty()) {
                    continue;
                }

                words.add(line.toLowerCase());

                count++;
                if (count > maxWords) {
                    throw new CustomExceptions.WordLimitExceededException("El archivo supera el límite permitido de " + maxWords + " palabras.");
                }
            }

            if (words.isEmpty()) {
                throw new CustomExceptions.NoValidWordsException("El archivo no contiene palabras válidas.");
            }

        } catch (IOException e) {
            throw new IOException("Error leyendo el archivo CSV: " + e.getMessage(), e);
        }

        return words;
    }


    /**
     * Funcion que recibe un set de String y genera un fichero .csv con los datos
     * @param results
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream generateCsvResults(Map<String, Boolean> results) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR, // Separador por defecto (,)
                     CSVWriter.NO_QUOTE_CHARACTER, // Sin comillas
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // Escribir la cabecera del CSV
            csvWriter.writeNext(new String[]{"word", "status"});

            for (Map.Entry<String, Boolean> entry : results.entrySet()) {
                String word = entry.getKey();
                String exists = entry.getValue() ? "true" : "false";
                csvWriter.writeNext(new String[]{word, exists});
            }
            csvWriter.flush();
        }
        return outputStream;
    }

}
