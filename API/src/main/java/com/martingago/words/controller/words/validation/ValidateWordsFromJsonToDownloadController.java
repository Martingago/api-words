package com.martingago.words.controller.words.validation;

import com.martingago.words.domain.service.word.CheckWordsInBatchesService;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.utils.CsvValidation;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Validar palabras",
        description = "Operaciones relacionadas con las validaciones de palabras en la API de WordRadar")
public class ValidateWordsFromJsonToDownloadController {

    private final CheckWordsInBatchesService checkWordsInBatchesService;
    private final CsvValidation csvValidation;

    @Operation(
            summary = "Validar palabras desde un body JSON y devolver resultado en CSV",
            description = """
        Recibe un body application/json con un array de palabras, valida su existencia en base de datos y devuelve un fichero CSV descargable con los resultados.

        📥 Formato de entrada:
        - Content-Type: application/json
        - Estructura: array de strings
 
        📑 Formato de archivo de salida:
        - CSV con dos columnas: 'word' y 'status'
        - 'word': palabra original
        - 'status': true o false según exista o no en la base de datos
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Archivo CSV generado correctamente con los resultados de la validación.",
                            content = @Content(
                                    mediaType = "application/octet-stream"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Body inválido. Array vacío, mal formateado o inexistente.",
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
                            description = "Error interno del servidor al procesar la solicitud.",
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
    @PostMapping(value = "/validate/body/csv", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> validateWordsJson(@RequestBody @NotEmpty List<String> words) throws IOException {
        Map<String, Boolean> validatedWords = checkWordsInBatchesService.checkWordsInBatches(new HashSet<>(words), 100);

        ByteArrayOutputStream outputStream = csvValidation.generateCsvResults(validatedWords);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("results.csv").build());

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

}
