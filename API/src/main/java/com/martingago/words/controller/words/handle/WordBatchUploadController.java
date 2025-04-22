package com.martingago.words.controller.words.handle;

import com.martingago.words.dto.docs.JobStatsWordsApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.batch.JobStatsDTO;
import com.martingago.words.mapper.batch.JobStatsMapper;
import com.martingago.words.utils.validations.JsonValidation;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Tag(   name = "Gestionar palabras",
        description = "Operaciones privadas relacionadas con la gestión de palabras en la API de WordRadar")

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

    @Operation(
            summary = "Subir un archivo JSONL con palabras para procesar en lote",
            description = """
        Permite subir un archivo de texto con formato JSONL (JSON Lines) donde cada línea contiene un objeto JSON representando una palabra y sus atributos. 
        El archivo se procesa mediante un job batch y se devuelve un resumen con estadísticas de la operación.

        📥 Formato de entrada:
        - Content-Type: multipart/form-data
        - Fichero con extensión .jsonl
        - Cada línea debe contener un objeto JSON válido
        - Ejemplo de contenido: \n
        {"word": "perro", "definitions": [...]} \n
        {"word": "gato", "definitions": [...]}
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Palabras procesadas y almacenadas correctamente en base de datos.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JobStatsWordsApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Archivo inválido o con formato de contenido incorrecto.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 400",
                                            value = ApiErrorExamples.ERROR_400
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar el archivo o al lanzar el job batch.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 500",
                                            value = ApiErrorExamples.ERROR_500
                                    )
                            )
                    )
            }
    )
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
