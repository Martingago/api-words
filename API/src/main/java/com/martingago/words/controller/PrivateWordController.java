package com.martingago.words.controller;

import com.martingago.words.dto.global.ApiResponse;
import com.martingago.words.dto.word.request.DeleteWordRequestDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.repository.LanguageRepository;
import com.martingago.words.service.batchInsertion.BatchProcessingInsertionService;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import com.martingago.words.utils.JsonValidation;
import com.netflix.discovery.converters.Auto;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/private")
public class PrivateWordController {

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

    @Autowired
    BatchProcessingInsertionService batchProcessingInsertionService;

    @Autowired
    JsonValidation jsonValidation;

    @Autowired
    WordService wordService;

    @Autowired
    LanguageService languageService;

    /**
     * Añade una palabra en la Base de datos.
     * @param fullWordResponseDTO
     * @return
     */
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponse<WordResponseViewDTO>> insertWord(
            @RequestBody @Valid FullWordRequestDTO fullWordResponseDTO){
        WordModel updatedWord= wordInsertionService.insertFullWord(fullWordResponseDTO);
        WordResponseViewDTO updatedWordResponseViewDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponse.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseViewDTO,
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
                    Map<String, WordResponseViewDTO> words = jsonValidation.parseFileToWordMap(file);
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

    /**
     * Elimina una palabra bajo un string específico
     * @param deleteWordRequestDTO
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Object>> deleteWordByString(
            @RequestBody @Valid DeleteWordRequestDTO deleteWordRequestDTO) {
        // Comprobar que el idioma sea válido
        languageService.searchLanguageByLangCode(deleteWordRequestDTO.getLangCode());

        //Compobar que la palabra en el idioma indicado exista en la BBDD
        WordModel wordToDelete = wordService.searchBasicWordWithLanguage(deleteWordRequestDTO.getWord(), deleteWordRequestDTO.getLangCode());

        //Eliminar la palabra de la base de datos
        wordService.deleteWordByWordModel(wordToDelete);

        //Si no se captura ningún error se crea la salida correspondiente
        return ApiResponse.build(true,
                "Word: '" + deleteWordRequestDTO.getWord() + "' successfully deleted",
                HttpStatus.OK.value(),
                deleteWordRequestDTO,
                HttpStatus.OK);
    }
}
