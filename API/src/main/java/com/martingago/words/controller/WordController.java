package com.martingago.words.controller;

import com.martingago.words.POJO.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.ScrapWordRequestDTO;
import com.martingago.words.dto.word.response.WordApiResponse;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.BaseWordRequestDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.dto.word.request.RelatedWordRequestDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.word.DailyWordService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import com.martingago.words.service.word.WordValidationService;
import com.martingago.words.utils.CsvValidation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
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

    @Autowired
    DailyWordService dailyWordService;

    /**
     * Busca en la base de datos una palabra
     *
     * @param word string de la palabra que se quiere buscar en la base de datos
     * @return Objeto ApiResponseDTO que contiene la información de la palabra encontrada.
     */
    @Operation(summary = "/search/{word}",
            description = "Método 'GET' que busca una palabra  específica en la API de WordRadar.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "404",
                            description = "Not found"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> findWordByName(
            @Parameter(description = "Palabra que quiere ser buscada en la Base de datos de WordRadar.",
            required = true,
            example = "piedra")
            @PathVariable String word
    ) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getWordByName(word);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }


    /**
     * Obtiene una palabra aleatoria de toda la base de datos de palabras
     *
     * @param wordLength tamaño de la palabra que se quiere obtener aleatoriamente
     * @return Objeto ApiResponseDTO que contiene la información de la palabra aleatoria obtenida.
     */
    @Operation(summary = "/word",
            description = "Método 'GET' que obtiene una palabra aleatoria de la API de WordRadar.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/word")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> getRandomWord(
            @Parameter(description = "Longitud de la palabra aleatoria",
                    required = false,
                    example = "5")
            @RequestParam(value = "length", required = false) Integer wordLength
    ) {
        WordResponseViewDTO wordResponseViewDTO = wordService.getRandomWord(wordLength);
        return ApiResponseDTO.build(true,
                "Word successfully founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }

    @Operation(summary = "/daily",
            description = "Método 'GET' que obtiene la palabra diaria generada por el servidor. La palabra diaria se genera a las 00:00 CET (Madrid)",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Bad request"),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @GetMapping("/daily")
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> getDailyWord() {
        WordModel wordModel = dailyWordService.getDailyWord();
        WordResponseViewDTO wordResponseViewDTO = wordMapper.toResponseDTO(wordModel);
        return ApiResponseDTO.build(true,
                "Daily word founded",
                HttpStatus.OK.value(),
                wordResponseViewDTO,
                HttpStatus.OK);
    }

    /**
     * Recibe un fichero .csv con un listado de palabras a comprobar en la base de datos
     *
     * @param file fichero que contiene palabras a buscar en la base de datos
     * @return fichero .csv con 2 columnas: word y status
     * @throws IOException
     */
    @Hidden
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
    @Hidden
    @PostMapping("/scrap-word")
    public ResponseEntity<ApiResponseDTO<Object>> scrapWord(@RequestBody ScrapWordRequestDTO scrapWordRequestDTO) {
        String baseWord = scrapWordRequestDTO.getWord();
        //Antes de iniciar el proceso de scrapping comprueba que la palabra no exista y si existe que sea un placeholder:
        WordValidator wordValidator = wordService.isWordLocatedAndNotPlaceholder(baseWord);

        if (wordValidator.isExists()) {
            return ApiResponseDTO.build(
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
            return ApiResponseDTO.build(
                    false,
                    "Couldn't add word '" + baseWord + "', did you mean: '" + relatedWordResponse.getRelatedWord() + "'?",
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    relatedWordResponse,
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        } else if (baseWordRequestDTO instanceof FullWordRequestDTO) {
            FullWordRequestDTO fullWordResponseDTO = (FullWordRequestDTO) baseWordRequestDTO;
            WordModel wordModel = wordInsertionService.insertFullWord(fullWordResponseDTO);


            return ApiResponseDTO.build(
                    true,
                    "Word successfully validate and added",
                    HttpStatus.CREATED.value(),
                    wordMapper.toResponseDTO(wordModel),
                    HttpStatus.CREATED);
        }
        return ApiResponseDTO.build(
                false,
                "Invalid Object to upload on database",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                baseWordRequestDTO,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
