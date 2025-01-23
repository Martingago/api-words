package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WordController {

    @Autowired
    WordService wordService;

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

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
     * Gets a random word from database
     *
     * @return
     */
    @GetMapping("/random-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> getRandomWord() {
        WordResponseDTO wordResponseDTO = wordService.getRandomWord();
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordResponseDTO,
                HttpStatus.FOUND);
    }

    /**
     * Inserta una palabra en la Base de datos.
     * @param wordResponseDTO
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> insertWord(@RequestBody  @Valid WordResponseDTO wordResponseDTO){
        wordInsertionService.insertFullWord(wordResponseDTO);
        //WordResponseDTO updatedWordResponseDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponse.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                null,
                HttpStatus.CREATED);
    }

}
