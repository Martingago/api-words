package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.WordDTO;
import com.martingago.words.service.word.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WordController {

    @Autowired
    WordService wordService;

    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponse<WordDTO>> findWordByName(@PathVariable String word) {
        WordDTO wordDTO = wordService.getWordByName(word);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordDTO,
                HttpStatus.FOUND);
    }


    /**
     * Gets a random word from database
     *
     * @return
     */
    @GetMapping("/random-word")
    public ResponseEntity<ApiResponse<WordDTO>> getRandomWord() {
        WordDTO wordDTO = wordService.getRandomWord();
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordDTO,
                HttpStatus.FOUND);
    }

}
