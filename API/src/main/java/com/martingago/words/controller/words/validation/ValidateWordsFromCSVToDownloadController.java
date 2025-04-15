package com.martingago.words.controller.words.validation;

import com.martingago.words.domain.service.word.CheckWordsInBatchesService;
import com.martingago.words.utils.CsvValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name ="Validar palabras",
        description = "Operaciones relacionadas con las validaciones de palabras en la API de WordRadar")
public class ValidateWordsFromCSVToDownloadController {

    private final CsvValidation csvValidation;
    private final CheckWordsInBatchesService checkWordsInBatchesService;

    /**
     * Recibe un fichero .csv con un listado de palabras a comprobar en la base de datos
     *
     * @param file fichero que contiene palabras a buscar en la base de datos
     * @return fichero .csv con 2 columnas: word y status
     * @throws IOException
     */

    @Operation(
            summary = "Validar palabras desde un fichero CSV y devolver resultado en CSV",
            description = "Recibe un archivo .csv o .txt con una palabra por línea, valida su existencia en la base de datos y devuelve un fichero CSV con el resultado. El archivo resultante contiene dos columnas: 'word' y 'exists', indicando si cada palabra existe en base de datos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Archivo CSV generado correctamente con los resultados de la validación.",
                            content = @Content(mediaType = "application/octet-stream")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar el archivo."
                    )
            }
    )
    @PostMapping(value = "/validate/file/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> validateWords(@RequestParam("file") MultipartFile file) throws IOException {
        //Procesa el fichero .csv y lo convierte a un set de Strings listo para procesar:
        Set<String> wordsToValidate = csvValidation.readWordsFromCsv(file);
        Map<String, Boolean> validatedWords = checkWordsInBatchesService.checkWordsInBatches(wordsToValidate,100);

        // Generar el archivo CSV de salida
        ByteArrayOutputStream outputStream = csvValidation.generateCsvResults(validatedWords);

        // Preparar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("results.csv").build());

        // Devolver el archivo como un array de bytes
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

    }

}
