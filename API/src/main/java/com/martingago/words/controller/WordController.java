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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            Map<String, WordResponseDTO> words = jsonValidation.parseJsonFileToWordMap(file);
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

    @PostMapping("/batch-multiple-files")
    public ResponseEntity<ApiResponse<List<String>>> insertMultipleJsonFiles(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            if (files.isEmpty()) {
                throw new BadRequestException("No files were uploaded");
            }

            List<String> processedFiles = new ArrayList<>();
            List<String> failedFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    if (file.isEmpty()) {
                        failedFiles.add(file.getOriginalFilename() + " (empty file)");
                        continue;
                    }

                    if (!jsonValidation.isValidJsonFile(file)) {
                        failedFiles.add(file.getOriginalFilename() + " (invalid JSON)");
                        continue;
                    }

                    Map<String, WordResponseDTO> words = jsonValidation.parseJsonFileToWordMap(file);
                    batchProcessingInsertionService.processJsonFile(words);
                    processedFiles.add(file.getOriginalFilename());

                } catch (Exception e) {
                    log.error("Error processing file {}: ", file.getOriginalFilename(), e);
                    failedFiles.add(file.getOriginalFilename() + " (" + e.getMessage() + ")");
                }
            }

            String message = buildResultMessage(processedFiles, failedFiles);

            return ApiResponse.build(
                    !processedFiles.isEmpty(),
                    message,
                    HttpStatus.CREATED.value(),
                    processedFiles,
                    HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error processing multiple files: ", e);
            return ApiResponse.error(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private String buildResultMessage(List<String> processedFiles, List<String> failedFiles) {
        StringBuilder message = new StringBuilder();

        if (!processedFiles.isEmpty()) {
            message.append("Successfully processed files: ")
                    .append(String.join(", ", processedFiles));
        }

        if (!failedFiles.isEmpty()) {
            if (message.length() > 0) {
                message.append(". ");
            }
            message.append("Failed to process files: ")
                    .append(String.join(", ", failedFiles));
        }

        return message.toString();
    }


}
