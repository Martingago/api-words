package com.martingago.words.controller;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.request.DeleteWordRequestDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.dto.word.request.FullWordRequestDTO;
import com.martingago.words.mapper.WordMapper;
import com.martingago.words.model.WordModel;
import com.martingago.words.service.language.LanguageService;
import com.martingago.words.service.word.WordInsertionService;
import com.martingago.words.service.word.WordService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/v1/private")
@Hidden
public class PrivateWordController {

    @Autowired
    Job job;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    WordInsertionService wordInsertionService;

    @Autowired
    WordMapper wordMapper;

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
    public ResponseEntity<ApiResponseDTO<WordResponseViewDTO>> insertWord(
            @RequestBody @Valid FullWordRequestDTO fullWordResponseDTO){
        WordModel updatedWord= wordInsertionService.insertFullWord(fullWordResponseDTO);
        WordResponseViewDTO updatedWordResponseViewDTO = wordMapper.toResponseDTO(updatedWord);
        return ApiResponseDTO.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordResponseViewDTO,
                HttpStatus.CREATED);
    }


    /**
     * Sube información de un fichero jsonl a la base de datos.
     * @param file MultipartFile que se va a añadir a la base de datos.
     * @return
     */
    @PostMapping("/upload-jsonl")
    public ResponseEntity<ApiResponseDTO<Entity>> uploadJsonlFile(@RequestParam("file") MultipartFile file) {
        try {
            // Crear un archivo temporal para almacenar el contenido subido
            long currentTime = System.currentTimeMillis();
            Path tempFile = Files.createTempFile("upload-"+ currentTime, ".jsonl");
            file.transferTo(tempFile.toFile());

            // Pasar la ruta del archivo como parámetro al job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("filePath", tempFile.toAbsolutePath().toString())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);

            // Limpieza del archivo temporal después de procesar
            Files.deleteIfExists(tempFile);

            return ApiResponseDTO.build(
                    true,
                    "Words successfully added",
                    HttpStatus.CREATED.value(),
                    null,
                    HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.build(
                    false,
                    e.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
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
    public ResponseEntity<ApiResponseDTO<Object>> deleteWordByString(
            @RequestBody @Valid DeleteWordRequestDTO deleteWordRequestDTO) {
        // Comprobar que el idioma sea válido
        languageService.searchLanguageByLangCode(deleteWordRequestDTO.getLangCode());

        //Compobar que la palabra en el idioma indicado exista en la BBDD
        WordModel wordToDelete = wordService.searchBasicWordWithLanguage(deleteWordRequestDTO.getWord(), deleteWordRequestDTO.getLangCode());

        //Eliminar la palabra de la base de datos
        wordService.deleteWordByWordModel(wordToDelete);

        //Si no se captura ningún error se crea la salida correspondiente
        return ApiResponseDTO.build(true,
                "Word: '" + deleteWordRequestDTO.getWord() + "' successfully deleted",
                HttpStatus.OK.value(),
                deleteWordRequestDTO,
                HttpStatus.OK);
    }
}
