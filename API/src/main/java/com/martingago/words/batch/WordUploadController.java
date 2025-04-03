package com.martingago.words.batch;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.word.response.WordResponseViewDTO;
import com.martingago.words.utils.JsonValidation;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WordUploadController {

    private final Job job;
    private final JobLauncher jobLauncher;

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

}
