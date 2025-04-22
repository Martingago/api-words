package com.martingago.words.controller.words.validation;


import com.martingago.words.domain.service.word.CheckWordsInBatchesService;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordValidationResponseExample;
import com.martingago.words.utils.validations.CsvValidation;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Validar palabras",
        description = "Operaciones relacionadas con las validaciones de palabras en la API de WordRadar")
public class ValidateWordsFromFileToDataController {

    private final CsvValidation csvValidation;
    private final CheckWordsInBatchesService checkWordsInBatchesService;

    /**
     * Endpoint que permite recibir un fichero .CSV y devolver la información procesada dentro del body de la response.
     * @param file fichero a procesar por la aplicación.
     * @return
     * @throws IOException
     */
    @Operation(summary = "Validar palabras desde un fichero CSV y devolver resultado en JSON",
            description = """
        Recibe un archivo .csv o .txt con una palabra por línea, sin cabecera, valida su existencia en la base de datos y devuelve los resultados en formato JSON.
        
        📄 Formato de archivo de entrada:
        - Extensiones permitidas: .csv, .txt
        - Una palabra por línea
        - Sin título de cabecera
        - Máximo: 10.000 palabras
        
        📤 Formato de respuesta:
        - JSON con un objeto 'responseObject' tipo Map<String, Boolean>
        - Cada palabra enviada como clave y true/false como valor según exista o no en la base de datos.
        """,
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordValidationResponseExample.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición mal formada.",
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
            })
    @PostMapping(value = "/validate/file/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<Map<String, Boolean>>> validateWordsFileAsJson(
            @Parameter(
                    description = "Archivo CSV o TXT que contiene una palabra por línea, sin cabecera. Máximo 10.000 palabras por archivo.",
                    required = true
            )
            @RequestParam("file") MultipartFile file) throws IOException {

        Set<String> wordsToValidate = csvValidation.readWordsFromCsv(file);
        Map<String, Boolean> validatedWords = checkWordsInBatchesService.checkWordsInBatches(wordsToValidate, 100);

        return ApiResponseDTO.build(true,
                "Words validated successfully.",
                HttpStatus.OK.value(),
                validatedWords,
                HttpStatus.OK);
    }

}
