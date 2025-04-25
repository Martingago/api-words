package com.martingago.words.controller.words.search;

import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.FilterWordsService;
import com.martingago.words.dto.docs.ListStringApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@Tag(   name ="Buscar palabras")
public class SearchWordFilterController {

    private final FilterWordsService filterWordsService;

    @Operation(
            summary = "Buscar palabras con filtros personalizados",
            description = """
        Este endpoint permite obtener un listado de palabras desde la base de datos aplicando múltiples filtros opcionales.

        📥 Filtros disponibles:
        - `startsWith`: palabras que comienzan por esta cadena.
        - `endsWith`: palabras que terminan con esta cadena.
        - `length`: longitud exacta de la palabra.
        - `langCode`: código de idioma de la palabra.
        - `qualifications`: lista de calificaciones que debe tener al menos una definición asociada a la palabra.

        Todos los parámetros son opcionales y combinables entre sí.

        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de palabras filtradas encontrado correctamente.",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListStringApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al ejecutar la consulta.",
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

    @GetMapping("/words/filter")
    public ResponseEntity<ApiResponseDTO<List<String>>> filterWords(
            @Parameter(description = "Filtrar palabras que empiecen por esta cadena de valores.", example = "a")
            @RequestParam(required = false) String startsWith,

            @Parameter(description = "Filtrar palabras que terminen por esta cadena de valores.", example = "r")
            @RequestParam(required = false) String endsWith,

            @Parameter(description = "Filtrar palabras con longitud exacta.", example = "5")
            @RequestParam(required = false) Integer length,

            @Parameter(description = "Filtrar palabras por código de idioma.", example = "esp")
            @RequestParam(required = false) String langCode,

            @Parameter(description = "Filtrar palabras por una lista de calificaciones.", example = "sustantivo masculino")
            @RequestParam(required = false) List<String> qualifications
    ){
        List<WordModel> filteredWords = filterWordsService.getWordsExtendedFilters(startsWith,endsWith,length,langCode, qualifications);
        List<String> responseDTOList = filteredWords.stream()
                .map(WordModel::getWord)
                .collect(Collectors.toList());

        return ApiResponseDTO.build(true,
                "Filtered words found successfully",
                HttpStatus.OK.value(),
                responseDTOList,
                HttpStatus.OK);
    }
}
