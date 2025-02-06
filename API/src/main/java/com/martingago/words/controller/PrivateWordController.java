package com.martingago.words.controller;

import com.martingago.words.dto.ApiResponse;
import com.martingago.words.dto.word.WordResponseDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.batchInsertion.BatchProcessingInsertionService;
import com.martingago.words.service.word.WordInsertionService;
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

@RestController
@RequestMapping("/api/v1/private")
@Slf4j
public class PrivateWordController {

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

    @Autowired
    BatchProcessingInsertionService batchProcessingInsertionService;

    @Autowired
    JsonValidation jsonValidation;



    /**
     * Inserta una palabra en la Base de datos.
     * @param wordResponseDTO
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponse<WordResponseDTO>> insertWord(@RequestBody @Valid WordResponseDTO wordResponseDTO){
        WordModel updatedWord= wordInsertionService.insertFullWord(wordResponseDTO);
        WordResponseDTO updatedWordResponseDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponse.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseDTO,
                HttpStatus.CREATED);
    }



    /**
     * Función que procesa la inserción de múltiples ficheros en la Base de datos
     * @param files Lista de ficheros a procesar
     */
    @PostMapping("/upload-words")
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
                    batchProcessingInsertionService.processAllJsonData(words);
                    processedFiles.add(file.getOriginalFilename());

                } catch (Exception e) {
                    log.error("Error processing file {}: ", file.getOriginalFilename(), e);
                    failedFiles.add(file.getOriginalFilename() + " (" + e.getMessage() + ")");
                }
            }

            String message = jsonValidation.buildResultMessage(processedFiles, failedFiles);

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
}
