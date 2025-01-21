package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.WordDTO;
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

    @GetMapping("/search/{word}")
    public ResponseEntity<ApiResponse<WordDTO>> findWordByName(@PathVariable String word){
        WordDTO wordDTO = wordService.getWordByName(word);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordDTO,
                HttpStatus.FOUND);
    }

    @GetMapping("/search-test/{word}")
    public ResponseEntity<ApiResponse<WordDTO>> findWordByNameTest(@PathVariable String word){
        WordDTO wordDTO = wordService.getWordByNameTest(word);
        return ApiResponse.build(true,
                "Word successfully founded",
                HttpStatus.FOUND.value(),
                wordDTO,
                HttpStatus.FOUND);
    }


    /**
     * Gets the daily word
     * @return
     */
//    @GetMapping("/daily-word")
//    public WordDTO getDailyWord(){
//        //Generate a random ID every day at 00:00
//
//        //Uses the random ID generated to request that word
//        return wordService.generateRandomWord();
//    }

    /**
     * Gets a random word from database
     * @return
     */
//    @GetMapping("/random-word")
//    public WordDTO getRandomWord(){
//        return wordService.generateRandomWord();
//    }

    /**
     * Adds a new word to the database
     * @param wordDTO
     * @return
     */
//    @PostMapping("/add-word")
//    public ResponseEntity<ApiResponse<WordDTO>> insertWord(@Valid @RequestBody WordDTO wordDTO){
//        WordDTO addedWord =  wordService.addNewWord(wordDTO);
//        return ApiResponse.build(
//                true,
//                "Word '" + addedWord.getWord() + "' was added successfully",
//                HttpStatus.CREATED.value(),
//                addedWord,
//                HttpStatus.CREATED);
//    }

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

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteCSV(@RequestParam("file") MultipartFile file){
        if(file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")){
            return  ResponseEntity.badRequest().body(null);
        }
        String deletedWords = wordService.deleteWordsFromCSV(file);
        return ApiResponse.build(true,
                deletedWords,
                HttpStatus.NO_CONTENT.value(),
                deletedWords,
                HttpStatus.NO_CONTENT);
    }
}
