package com.martingago.words.controller.words;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.batch.JobStatsDTO;
import com.martingago.words.mapper.batch.JobStatsMapper;
import com.martingago.words.utils.JsonValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/private")
@RequiredArgsConstructor
public class WordBatchUploadController {

    private final Job job;
    private final JobLauncher jobLauncher;
    private final JobStatsMapper jobStatsMapper;
    private final JsonValidation jsonValidation;

    /**
     * Endpoint que sube palabras a la base de datos pasadas a través de un fichero JSONL
     * @param file MultipartFile a subir como datos a la base de datos.
     * @return
     */
    @PostMapping("/upload-jsonl")
    public ResponseEntity<ApiResponseDTO<JobStatsDTO>> uploadJsonlFile(
            @RequestParam("file") MultipartFile file) {
        Path tempFile = null;

        try {

            // Validar fichero antes de cualquier cosa
            if (!jsonValidation.isValidJsonFile(file)) {
                return ApiResponseDTO.build(
                        false,
                        "Invalid file type. Must be a .jsonl file with application/json content type.",
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!jsonValidation.isProperJsonlContent(file)) {
                return ApiResponseDTO.build(
                        false,
                        "Invalid file content. Each line must be a valid JSON object.",
                        HttpStatus.BAD_REQUEST.value(),
                        null,
                        HttpStatus.BAD_REQUEST
                );
            }


            // Crear archivo temporal para el JSONL subido
            long currentTime = System.currentTimeMillis();
            tempFile = Files.createTempFile("upload-" + currentTime, ".jsonl");
            file.transferTo(tempFile.toFile());

            // Preparar parámetros para el job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", currentTime)
                    .addString("filePath", tempFile.toAbsolutePath().toString())
                    .toJobParameters();

            // Lanzar el job
            JobExecution execution = jobLauncher.run(job, jobParameters);

            return ApiResponseDTO.build(
                    true,
                    "Words successfully added",
                    HttpStatus.CREATED.value(),
                    jobStatsMapper.map(execution),
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            //Capturar los errores que pudieron producirse en la ejecución del job:

            return ApiResponseDTO.build(
                    false,
                    "Error while processing file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } finally {
            // Limpiar el archivo temporal pase lo que pase
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception deleteError) {
                    System.err.println("Couldn't delete temp file: " + deleteError.getMessage());
                }
            }
        }
    }


}
