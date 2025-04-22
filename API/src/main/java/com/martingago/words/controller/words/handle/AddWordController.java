package com.martingago.words.controller.words.handle;

import com.martingago.words.domain.service.batch.ProcessWordModelService;
import com.martingago.words.domain.service.word.WordService;
import com.martingago.words.dto.docs.WordApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.word.WordDTO;
import com.martingago.words.mapper.models.WordMapper;
import com.martingago.words.domain.model.WordModel;
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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/private")
@Tag(   name = "Gestionar palabras",
        description = "Operaciones privadas relacionadas con la gestión de palabras en la API de WordRadar")
public class AddWordController {

    private final ProcessWordModelService processWordModelService;
    private final WordService wordService;
    private final WordMapper wordMapper;


    /**
     * Añade una palabra a la base de datos
     *
     * @param wordDTO objeto DTO que se quiere añadir a la base de datos.
     * @return ApiResponseDTO que contiene un WordDTO con la información que se ha añadido en la base de datos.
     */
    @Operation(
            summary = "Añadir una nueva palabra",
            description = "Método 'POST' para añadir una nueva palabra a la base de datos. El proceso involucra la validación y la creación de las referencias necesarias para la persistencia.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Palabra añadida correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WordApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición mal formada o datos incorrectos.",
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
    @PostMapping("/add-word")
    public ResponseEntity<ApiResponseDTO<WordDTO>> insertWord(
            @RequestBody @Valid WordDTO wordDTO){

        //Valida y crea el objeto WordModel en la base de datos
        WordModel wordModel = processWordModelService.processWordDTO(wordDTO);

        //Persiste la entidad en la base de datos.
        WordModel insertedWord = wordService.saveWordModel(wordModel);

        //Devuelve el objeto procesado al usuario.
        WordDTO updatedWordDTO = wordMapper.toResponseDTO(insertedWord);
        return ApiResponseDTO.build(true,
                "Word successfully created",
                HttpStatus.CREATED.value(),
                updatedWordDTO,
                HttpStatus.CREATED);
    }


}
