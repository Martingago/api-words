package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.WordResponseDTO;
import com.martingago.words.service.WordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WordController {

    @Autowired
    WordService wordService;

    /**
     * Gets the daily word
     * @return
     */
    @GetMapping("/daily-word")
    public WordResponseDTO getDailyWord(){
        //Generate a random ID every day at 00:00

        //Uses the random ID generated to request that word
        return wordService.generateRandomWord();
    }

    /**
     * Gets a random word from database
     * @return
     */
    @GetMapping("/random-word")
    public WordResponseDTO getRandomWord(){
        return wordService.generateRandomWord();
    }

    /**
     * Adds a new word to the database
     * @param wordResponseDTO
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> insertWord(@Valid @RequestBody WordResponseDTO wordResponseDTO){
        WordResponseDTO addedWord =  wordService.addNewWord(wordResponseDTO);
        return ApiResponse.build(
                true,
                "Word '" + addedWord.getWord() + "' was added successfully",
                HttpStatus.CREATED.value(),
                addedWord,
                HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body(null);
        }

        String processedWords = wordService.uploadWordsCSV(file);
        return ApiResponse.build(true,
                processedWords,
                HttpStatus.CREATED.value(),
                processedWords,
                HttpStatus.CREATED);
    }
}
