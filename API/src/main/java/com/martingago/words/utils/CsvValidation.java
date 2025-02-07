package com.martingago.words.utils;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Component
public class CsvValidation {

    /**
     * Lee un archivo .csv y devuelve un set con las palabras que existen en el
     * @param file
     * @return
     * @throws IOException
     */
    public Set<String> readWordsFromCsv(MultipartFile file) throws IOException{
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
        }catch (IOException e){
            throw new IOException("Error reading CSV file: " + e.getMessage());
        }
        return words;
    }

    /**
     * Funcion que recibe un set de String y genera un fichero .csv con los datos
     * @param results
     * @return
     * @throws IOException
     */
    public ByteArrayOutputStream generateCsvResults(Set<String[]> results) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR, // Separador por defecto (,)
                     CSVWriter.NO_QUOTE_CHARACTER, // Sin comillas
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // Escribir la cabecera del CSV
            csvWriter.writeNext(new String[]{"word", "status"});

            // Escribir los resultados
            for (String[] result : results) {
                csvWriter.writeNext(result);
            }
            csvWriter.flush(); // Asegurarse de que todo se escriba al buffer
        }
        return outputStream;
    }

}
