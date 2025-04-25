package com.martingago.words.controller.qualifications;

import com.martingago.words.domain.model.WordQualificationModel;
import com.martingago.words.domain.service.qualification.WordQualificationService;
import com.martingago.words.dto.docs.QualificationListApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.models.qualification.WordQualificationDTO;
import com.martingago.words.mapper.models.WordQualificationMapper;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(   name ="Clasificaciones", description = "Controlador que permite obtener información de las clasificaciones existentes en la base de datos.")
public class GetQualificationsController {

    private final WordQualificationService wordQualificationService;
    private final WordQualificationMapper wordQualificationMapper;


    @Operation(
            summary = "Obtener clasificaciones gramaticales de palabras",
            description = """
        Método `GET` que devuelve todas las clasificaciones disponibles en la base de datos de WordRadar.

        📌 Este endpoint puede ser útil para:
        - Mostrar filtros de búsqueda en UI
        - Validar nuevas palabras al añadirlas
        - Presentar categorías lingüísticas al usuario final
        """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de clasificaciones obtenido correctamente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = QualificationListApiResponseExample.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor al intentar obtener las clasificaciones.",
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
    @GetMapping("/qualifications")
    public ResponseEntity<ApiResponseDTO<List<WordQualificationDTO>>> getQualificationsFromDatabase(){
        List<WordQualificationDTO>  wordQualificationModelList = wordQualificationService.getAllQualificationsModel()
                .stream().map(wordQualificationMapper::toDTO)
                .toList();

        return ApiResponseDTO.build(true,
                "Word qualifications successfully founded",
                HttpStatus.OK.value(),
                wordQualificationModelList,
                HttpStatus.OK
                );
    }
}
