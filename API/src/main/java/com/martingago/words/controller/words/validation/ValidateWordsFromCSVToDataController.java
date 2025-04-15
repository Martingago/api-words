package com.martingago.words.controller.words.validation;


import com.martingago.words.domain.service.word.CheckWordsInBatchesService;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.WordValidationResponseExample;
import com.martingago.words.utils.CsvValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
public class ValidateWordsFromCSVToDataController {

    private final CsvValidation csvValidation;
    private final CheckWordsInBatchesService checkWordsInBatchesService;

    /**
     * Endpoint que permite recibir un fichero .CSV y devolver la información procesada dentro del body de la response.
     * @param file fichero a procesar por la aplicación.
     * @return
     * @throws IOException
     */
    @Operation(summary = "Validar palabras desde un fichero CSV y devolver resultado en JSON",
            description = "Recibe un archivo .csv o .txt con una palabra por línea, valida su existencia en la base de datos y devuelve los resultados en formato JSON.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Ok",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordValidationResponseExample.class))),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error")
            })
    @PostMapping(value = "/validate/file/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<Map<String, Boolean>>> validateWordsFileAsJson(
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
