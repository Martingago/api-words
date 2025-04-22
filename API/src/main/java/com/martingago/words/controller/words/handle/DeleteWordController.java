package com.martingago.words.controller.words.handle;

import com.martingago.words.dto.docs.WordDeleteApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.DeleteWordRequestDTO;
import com.martingago.words.domain.model.WordModel;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Tag(   name = "Gestionar palabras",
        description = "Operaciones privadas relacionadas con la gestión de palabras en la API de WordRadar")
public class DeleteWordController {

    private final WordService wordService;


    /**
     * Elimina de la base de datos una palabra asociada a un idioma en concreto
     * @param deleteWordRequestDTO objeto que contiene el codigo de idioma de la palabra + string de la palabra a eliminar.
     * @return ApiResponseDTO, que contiene los datos de la palabra eliminada y indicando el estado de la operación.
     */
    @Operation(
            summary = "Eliminar una palabra existente de la base de datos",
            description = "Elimina una palabra de la base de datos en un idioma concreto. Solo accesible para operaciones privadas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Palabra eliminada correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordDeleteApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida. Faltan datos o el JSON de entrada no es correcto.",
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
                            description = "La palabra indicada no existe en base de datos para el idioma proporcionado.",
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
                            responseCode = "500",
                            description = "Error interno del servidor al procesar la eliminación.",
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

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseDTO<DeleteWordRequestDTO>> deleteWordByString(
            @RequestBody @Valid DeleteWordRequestDTO deleteWordRequestDTO) {

        //Comprobar que la palabra en el idioma indicado exista en la BBDD
        WordModel wordToDelete = wordService.searchBasicWordWithLanguage(deleteWordRequestDTO.getWord(), deleteWordRequestDTO.getLangCode());

        //Eliminar la palabra de la base de datos
        wordService.deleteWordByWordModel(wordToDelete);

        //Si no se captura ningún error se crea la salida correspondiente
        return ApiResponseDTO.build(true,
                "Word: '" + deleteWordRequestDTO.getWord() + "' successfully deleted",
                HttpStatus.OK.value(),
                deleteWordRequestDTO,
                HttpStatus.OK);
    }
}
