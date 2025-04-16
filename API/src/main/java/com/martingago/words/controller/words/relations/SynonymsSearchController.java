package com.martingago.words.controller.words.relations;

import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.docs.ListStringApiResponseExample;
import com.martingago.words.domain.model.RelationEnumType;
import com.martingago.words.domain.service.relation.WordRelationService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@Tag(name = "Relaciones de palabras",
        description = "Operaciones relacionadas con la búsqueda de relaciones antónimas/sinónimas de palabras con otras.")
public class SynonymsSearchController {


    private final WordRelationService wordRelationService;

    /**
     * Controller que recibe como parámetro una palabra y un código de idioma y realiza una búsqueda de sus antónimos.
     *
     * @param word     palabra sobre la que se quieren buscar los sinónimos
     * @param langCode código de idioma de la palabra, por defecto 'esp'
     * @return ApiResponse que tien un listado con las palabras que mantiene relación de sinónimos.
     */
    @Operation(
            summary = "Obtener sinónimos de una palabra",
            description = "Método 'GET' que busca los sinónimos de una palabra concreta en base de datos, permitiendo especificar un código de idioma opcional.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de sinónimos encontrados.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListStringApiResponseExample.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parámetro de búsqueda no válido.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 400",
                                            value = ApiErrorExamples.ERROR_400
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron sinónimos para la palabra solicitada.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 404",
                                            value = ApiErrorExamples.ERROR_404
                                    ))

                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al procesar la solicitud.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WordErrorApiResponseExample.class),
                                    examples = @ExampleObject(
                                            name = "Error 500",
                                            value = ApiErrorExamples.ERROR_500
                                    ))
                    )
            }
    )

    @GetMapping("/synonyms/{word}")
    public ResponseEntity<ApiResponseDTO<List<String>>> findSynonymsByWord(
            @Parameter(description = "Palabra de la que se quieren buscar los sinónimos.",
                    required = true,
                    example = "oscuro")
            @PathVariable String word,

            @Parameter(description = "Código de idioma de la palabra sobre la que realizar la búsqueda.",
                    required = false,
                    example = "esp")
            @RequestParam(value = "lang", defaultValue = "esp") String langCode) {

        //Obtiene el listado de sinónimos de la palabra pasada como parámetro
        List<String> listSynonyms = wordRelationService.getRelationTypeByWord(word, RelationEnumType.SINONIMA, langCode);
        return ApiResponseDTO.build(
                true,
                "Synonyms founded ",
                HttpStatus.OK.value(),
                listSynonyms,
                HttpStatus.OK
        );

    }

}
