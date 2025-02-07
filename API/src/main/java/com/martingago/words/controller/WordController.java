package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.batchInsertion.BatchProcessingInsertionService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import com.martingago.words.service.word.WordValidationService;
import com.martingago.words.utils.CsvValidation;
import com.martingago.words.utils.JsonValidation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class WordController {

    @Autowired
    WordService wordService;

    @Autowired
    CsvValidation csvValidation;

    @Autowired
    WordValidationService wordValidationService;

    /**
     * Busca en la base de datos una palabra
     * @param word string de la palabra que se quiere buscar en la base de datos
     * @return Objeto ApiResponse que contiene la información de la palabra encontrada.
     */
    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponse<WordResponseDTO>> findWordByName(@PathVariable String word) {
        WordResponseDTO wordResponseDTO = wordService.getWordByName(word);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseDTO,
                HttpStatus.FOUND);
    }


    /**
     * Obtiene una palabra aleatoria de toda la base de datos de palabras
     * @param langCode idioma sobre el que se quiere obtener la palabra aleatoria
     * @return Objeto ApiResponse que contiene la información de la palabra aleatoria obtenida.
     */
    @GetMapping("/word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> getRandomWord(@RequestParam("lang") String langCode) {
        WordResponseDTO wordResponseDTO = wordService.getRandomWord(langCode);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseDTO,
                HttpStatus.FOUND);
    }

    /**
     * Recibe un fichero .csv con un listado de palabras a comprobar en la base de datos
     * @param file fichero que contiene palabras a buscar en la base de datos
     * @return fichero .csv con 2 columnas: word y status
     * @throws IOException
     */
    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> validateWords(@RequestParam("file") MultipartFile file) throws  IOException{
        //Procesa el fichero .csv y lo convierte a un set de Strings:
        Set<String> wordsToValidate = csvValidation.readWordsFromCsv(file);
        //Procesa mediante bath y obtiene un set que contiene la palabra y su estado en la BBDD.
        Set<String[]> wordResultValidation = wordValidationService.processWordsInBatches(wordsToValidate);
        // Generar el archivo CSV de salida
        ByteArrayOutputStream outputStream = csvValidation.generateCsvResults(wordResultValidation);

        // Preparar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("results.csv").build());

        // Devolver el archivo como un array de bytes
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

    }
}
