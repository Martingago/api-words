package com.martingago.words.controller.words;

import com.martingago.words.context.WordValidator;
import com.martingago.words.client.MyScrapWordClient;
import com.martingago.words.domain.service.batch.ProcessWordModelService;
import com.martingago.words.domain.service.microservice.WordMicroserviceService;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.WordDTOExternal;
import com.martingago.words.dto.microservices.word.external.RelatedWordDTOExternal;
import com.martingago.words.dto.microservices.word.external.WordToScrapDTOExternal;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@Tag(   name = "Gestionar palabras",
        description = "Operaciones privadas relacionadas con la gestión de palabras en la API de WordRadar")
public class AddWordMicroserviceController {

    private final ProcessWordModelService processWordModelService;
    private final WordService wordService;
    private final WordMicroserviceService wordMicroserviceService;
    private final WordMapper wordMapper;

    /**
     *  Scrapea una palabra recibida por el usuario, la añade a la base de datos, y se la muestra al usuario
     *  Valida que la palabra exista en la base de datos, si ya existe, informa al usuario.
     *  En caso de no existir, llama al microservicio de scraping para procesar la palabra y añadirla a la base de datos.
     * @param word string de la palabra que se quiere añadir en la base de datos.
     * @return
     */

    @Operation(
            summary = "Scrapear y añadir una palabra desde microservicio externo",
            description = """
        Recibe una palabra desde el cliente, comprueba si ya existe en base de datos y, si no existe, la obtiene desde un microservicio de scraping.
        Posteriormente, la procesa y la añade a la base de datos.

        📥 Formato de entrada:
        - Content-Type: application/json
        - Estructura:  
        {"word": "palabra_a_añadir"}
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Palabra scrapeada y añadida correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "La palabra ya existe en base de datos.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 409",
                                            value = ApiErrorExamples.ERROR_409
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Palabra no encontrada/procesada, pero se sugiere una similar.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation =  WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 422",
                                            value = ApiErrorExamples.ERROR_422
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al procesar la solicitud o respuesta inválida del microservicio.",
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

    @PostMapping("/scrap-word")
    public ResponseEntity<ApiResponseDTO<Object>> scrapWord(@RequestBody WordToScrapDTOExternal word) {

        //Antes de iniciar el proceso de scrapping comprueba que la palabra no exista y si existe que sea un placeholder:
        WordValidator wordValidator = wordService.isWordLocatedAndNotPlaceholder(word.getWord());

        if (wordValidator.isExists()) {
            return ApiResponseDTO.build(
                    false,
                    "Word already exists on database",
                    HttpStatus.CONFLICT.value(),
                    wordMapper.toResponseDTO(wordValidator.getWordModel()),
                    HttpStatus.CONFLICT
            );
        }

        //Si no encuentra la palabra usa el micro-servicio y obtiene el objeto WordDTO para subir.
        WordDTO wordDTO = wordMicroserviceService.findWordInMicroservice(word.getWord());

        //Procesa el DTO y lo convierte en una entidad para persistir en la base de datos.
        WordModel wordModel = processWordModelService.processWordDTO(wordDTO);

        //Guarda la entidad en la base de datos.
        wordService.saveWordModel(wordModel);

        return ApiResponseDTO.build(
                true,
                "Word successfully validate and added",
                HttpStatus.CREATED.value(),
                wordMapper.toResponseDTO(wordModel),
                HttpStatus.CREATED);
    }


}
