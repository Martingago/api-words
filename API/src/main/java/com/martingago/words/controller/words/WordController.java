package com.martingago.words.controller.words;

import com.martingago.words.context.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.domain.service.word.CreateWordModelService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;
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
     * Función que recibe una palabra > valida que no exista en la Base de datos > la scrapea > la sube a la BBDD
     *
     * @param scrapWordRequestDTO String de la palabra que se quiere validar y scrapear
     * @return
     */
    @Hidden
    @PostMapping("/scrap-word")
    public ResponseEntity<ApiResponseDTO<Object>> scrapWord(@RequestBody String word) {

        //Antes de iniciar el proceso de scrapping comprueba que la palabra no exista y si existe que sea un placeholder:
        WordValidator wordValidator = wordService.isWordLocatedAndNotPlaceholder(word);

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
        ExternalBaseWordDTO externalBaseWordDTO = myScrapWordClient.procesarPalabra(word);
        // Comprueba si lo que recibe del microservicio es una full o related word
        if (externalBaseWordDTO instanceof RelatedWordDTOExternal) {
            RelatedWordDTOExternal relatedWordResponse = (RelatedWordDTOExternal) externalBaseWordDTO;
            return ApiResponseDTO.build(
                    false,
                    "Couldn't add word '" + word + "', did you mean: '" + relatedWordResponse.getRelatedWord() + "'?",
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    relatedWordResponse,
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        } else if (externalBaseWordDTO instanceof WordDTOExternal) {
            WordDTOExternal fullWordResponseDTO = (WordDTOExternal) externalBaseWordDTO;

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
                externalBaseWordDTO,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
