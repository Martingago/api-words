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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@Slf4j
public class WordController {

    @Autowired
    WordService wordService;

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

    @Autowired
    BatchProcessingInsertionService batchProcessingInsertionService;

    @Autowired
    JsonValidation jsonValidation;

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
        WordModel updatedWord= wordInsertionService.insertFullWord(wordResponseDTO);
        WordResponseDTO updatedWordResponseDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponse.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseDTO,
                HttpStatus.CREATED);
    }

    @PostMapping("/batch-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> insertBatchWords(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("File JSON is empty");
            }

            if (!jsonValidation.isValidJsonFile(file)) {
                throw new BadRequestException("Please upload a valid JSON file");
            }

            //Llamar a la función de BatchProcessingInsertionService
            Set<WordResponseDTO> words = jsonValidation.parseJsonFileToWordSet(file);
            batchProcessingInsertionService.processJsonFile(words);

            return ApiResponse.build(
                    true,
                    "Words Successfully added",
                    HttpStatus.CREATED.value(),
                    null,
                    HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error processing file upload: ", e);
            return ApiResponse.error(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


}
