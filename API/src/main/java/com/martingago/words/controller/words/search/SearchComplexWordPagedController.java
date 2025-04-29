package com.martingago.words.controller.words.search;

import com.martingago.words.config.PaginationProperties;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.FilterWordsService;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.docs.WordPageStringApiResponseExample;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@Tag(name = "Buscar palabras paginadas", description = "Buscar palabras filtradas con paginación incluida.")
public class SearchComplexWordPagedController {

    private final FilterWordsService filterWordsService;
    private final PaginationProperties paginationProperties;
    private final WordMapper wordMapper;

    @Operation(
            summary = "Buscar palabras paginadas con filtros personalizados",
            description = """
        Este endpoint permite obtener un listado de palabras desde la base de datos aplicando múltiples filtros opcionales.

        📥 Filtros disponibles:
        - `startsWith`: palabras que comienzan por esta cadena.
        - `endsWith`: palabras que terminan con esta cadena.
        - `length`: longitud exacta de la palabra.
        - `langCode`: código de idioma de la palabra.
        - `qualifications`: lista de calificaciones que debe tener al menos una definición asociada a la palabra.
        - `page`: Número de pagína a mostrar
        - `size` : Número de elementos a mostrar por página

        Todos los parámetros son opcionales y combinables entre sí.

        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de palabras paginadas filtradas encontrado correctamente.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordPageStringApiResponseExample.class)
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

    @GetMapping("/words/details")
    public ResponseEntity<ApiResponseDTO<Page<WordDTO>>> getFilteredWordsPaged(
            @Parameter(description = "Prefijo por el que debe comenzar la palabra.") @RequestParam(required = false) String startsWith,
            @Parameter(description = "Sufijo por el que debe terminar la palabra.") @RequestParam(required = false) String endsWith,
            @Parameter(description = "Longitud exacta de la palabra.") @RequestParam(required = false) Integer length,
            @Parameter(description = "Código de idioma de la palabra.") @RequestParam(required = false) String langCode,
            @Parameter(description = "Lista de calificativos que debe tener la palabra.") @RequestParam(required = false) List<String> qualifications,
            @Parameter(description = "Número de página (por defecto 0).") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (por defecto 2500). Máximo 5000") @RequestParam(defaultValue = "2500") int size
    ) {
        int maxSize = paginationProperties.getMaxComplexWords();
        int safeSize = Math.min(size, maxSize); //Limita de forma segura el tamaño máximo de consulta

        Pageable pageable = PageRequest.of(page, safeSize);
        Page<WordModel> filteredPage = filterWordsService.getPaginatedFilteredComplexWords(
                startsWith, endsWith, length, langCode, qualifications, pageable);

        Page<WordDTO> wordPage = filteredPage.map(wordMapper::toResponseDTO);

        return ApiResponseDTO.build(true,
                "Filtered paginated words found successfully",
                HttpStatus.OK.value(),
                wordPage,
                HttpStatus.OK);
    }

}


