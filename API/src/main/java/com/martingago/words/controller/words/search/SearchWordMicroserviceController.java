package com.martingago.words.controller.words.search;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.batch.ProcessWordModelService;
import com.martingago.words.domain.service.microservice.WordMicroserviceService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.docs.WordRelatedFoundedApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1")
@Tag(   name ="Buscar palabras",
        description = "Operaciones relacionadas con la búsqueda de palabras en la API de WordRadar")
public class SearchWordMicroserviceController {

    private final WordService wordService;
    private final ProcessWordModelService processWordModelService;
    private final WordMicroserviceService wordMicroserviceService;

    @Operation(
            summary = "Buscar una palabra con búsqueda profunda (fallback a microservicio)",
            description = """
        Método 'GET' que busca una palabra específica en la base de datos de WordRadar. 
        Si no está registrada, invoca un microservicio externo que scrapea la información de la palabra, 
        la procesa, la almacena en base de datos y devuelve la información al usuario.

        ⚠️ Nota: este endpoint puede demorar más si la palabra no existe previamente.

        📥 Formato de entrada:
        - PathParam: palabra a buscar
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Palabra encontrada en base de datos o scrapeada correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetro de búsqueda no válido.",
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
                            responseCode = "404",
                            description = "No se encontró la palabra solicitada.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 404",
                                            value = ApiErrorExamples.ERROR_404
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Palabra relacionada encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordRelatedFoundedApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 422",
                                            value = ApiErrorExamples.ERROR_422_RELATED_WORD
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor.",
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

    @GetMapping("/words/{word}/deep")
    public ResponseEntity<ApiResponseDTO<Object>> findWordByName(
            @Parameter(description = "Palabra que se desea buscar en la base de datos de WordRadar.",
                    required = true,
                    example = "piedra")
            @PathVariable String word
    ) {
        try {
            WordDTO wordDTO = wordService.getWordByName(word);
            return ApiResponseDTO.build(true,
                    "Word successfully founded",
                    HttpStatus.OK.value(),
                    wordDTO,
                    HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            //Si no encuentra la palabra la scrapea, la carga en la base de datos, y se la envia al usuario
            WordDTO wordDTO = wordMicroserviceService.findWordInMicroservice(word);
            WordModel wordModel = processWordModelService.processWordDTO(wordDTO);

            //Guarda la entidad en la base de datos.
            wordService.saveWordModel(wordModel);

            //Devuelve la palabra scrapeada y cargada de la base de datos.
            return ApiResponseDTO.build(true,
                    "Word successfully founded",
                    HttpStatus.OK.value(),
                    wordDTO,
                    HttpStatus.OK);
        }
    }

}
