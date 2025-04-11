package com.martingago.words.controller.words;

import com.martingago.words.context.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.ScrapWordRequestDTO;
import com.martingago.words.dto.word.request.BaseWordRequestDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.dto.word.request.RelatedWordRequestDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.utils.CsvValidation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
@Slf4j
public class WordController {

    private final CreateWordModelService createWordModelService;

    @Autowired
    WordService wordService;

    @Autowired
    CsvValidation csvValidation;

    @Autowired
    MyScrapWordClient myScrapWordClient;

    @Autowired
    WordMapper wordMapper;


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
        Set<String[]> wordResultValidation = new HashSet<>();
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

            WordModel wordModel = null;
            //WordModel wordModel = createWordModelService.processWordDTOintoWordModel(fullWordResponseDTO);


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
