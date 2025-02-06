package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.batchInsertion.BatchProcessingInsertionService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import com.martingago.words.utils.JsonValidation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping("/random-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> getRandomWord(@RequestParam("lang") String langCode) {
        WordResponseDTO wordResponseDTO = wordService.getRandomWord(langCode);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseDTO,
                HttpStatus.FOUND);
    }








}
