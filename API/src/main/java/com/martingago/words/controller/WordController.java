package com.martingago.words.controller;

import com.martingago.words.POJO.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.dto.global.ApiResponse;
import com.martingago.words.dto.word.request.ScrapWordRequestDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.BaseWordRequestDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.dto.word.request.RelatedWordRequestDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import com.martingago.words.service.word.WordValidationService;
import com.martingago.words.utils.CsvValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    MyScrapWordClient myScrapWordClient;

    @Autowired
    WordMapper wordMapper;

    @Autowired
    WordInsertionService wordInsertionService;

    /**
     * Busca en la base de datos una palabra
     *
     * @param word string de la palabra que se quiere buscar en la base de datos
     * @return Objeto ApiResponse que contiene la información de la palabra encontrada.
     */
    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponse<WordResponseViewDTO>> findWordByName(@PathVariable String word) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getWordByName(word);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseViewDTO,
                HttpStatus.FOUND);
    }


    /**
     * Obtiene una palabra aleatoria de toda la base de datos de palabras
     *
     * @param langCode idioma sobre el que se quiere obtener la palabra aleatoria
     * @return Objeto ApiResponse que contiene la información de la palabra aleatoria obtenida.
     */
    @GetMapping("/word")
    public ResponseEntity<ApiResponse<WordResponseViewDTO>> getRandomWord(@RequestParam("lang") String langCode) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getRandomWord(langCode);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseViewDTO,
                HttpStatus.FOUND);
    }

    /**
     * Recibe un fichero .csv con un listado de palabras a comprobar en la base de datos
     *
     * @param file fichero que contiene palabras a buscar en la base de datos
     * @return fichero .csv con 2 columnas: word y status
     * @throws IOException
     */
    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> validateWords(@RequestParam("file") MultipartFile file) throws IOException {
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


    /**
     * Función que recibe una palabra > valida que no exista en la Base de datos > la scrapea > la sube a la BBDD
     *
     * @param scrapWordRequestDTO String de la palabra que se quiere validar y scrapear
     * @return
     */
    @PostMapping("/scrap-word")
    public ResponseEntity<ApiResponse<Object>> scrapWord(@RequestBody ScrapWordRequestDTO scrapWordRequestDTO) {
        String baseWord = scrapWordRequestDTO.getWord();
        //Antes de iniciar el proceso de scrapping comprueba que la palabra no exista y si existe que sea un placeholder:
        WordValidator wordValidator = wordService.isWordLocatedAndNotPlaceholder(baseWord);

        if (wordValidator.isExists()) {
            return ApiResponse.build(
                    true,
                    "Word already exists on database",
                    HttpStatus.CONFLICT.value(),
                    wordMapper.toResponseDTO(wordValidator.getWordModel()),
                    HttpStatus.CONFLICT
            );
        }

        //Si no encuentra la palabra usa el micro-servicio > procesa > sube palabra
        BaseWordRequestDTO baseWordRequestDTO = myScrapWordClient.procesarPalabra(scrapWordRequestDTO);
        // Comprueba si lo que recibe del microservicio es una full o related word
        if (baseWordRequestDTO instanceof RelatedWordRequestDTO) {
            RelatedWordRequestDTO relatedWordResponse = (RelatedWordRequestDTO) baseWordRequestDTO;
            return ApiResponse.build(
                    false,
                    "Couldn't add word '" + baseWord + "', did you mean: '" + relatedWordResponse.getRelatedWord() + "'?",
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    relatedWordResponse,
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        } else if (baseWordRequestDTO instanceof FullWordRequestDTO) {
            FullWordRequestDTO fullWordResponseDTO = (FullWordRequestDTO) baseWordRequestDTO;
            WordModel wordModel = wordInsertionService.insertFullWord(fullWordResponseDTO);


            return ApiResponse.build(
                    true,
                    "Word successfully validate and added",
                    HttpStatus.CREATED.value(),
                    wordMapper.toResponseDTO(wordModel),
                    HttpStatus.CREATED);
        }
        return ApiResponse.build(
                false,
                "Invalid Object to upload on database",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                baseWordRequestDTO,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
