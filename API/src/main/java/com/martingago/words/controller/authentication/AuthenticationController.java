package com.martingago.words.controller.authentication;

import com.martingago.words.dto.docs.UserAuthenticacionApiResponseExample;
import com.martingago.words.dto.docs.WordErrorApiResponseExample;
import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.authentication.AuthLoginRequestDTO;
import com.martingago.words.dto.authentication.AuthResponseDTO;
import com.martingago.words.domain.service.user.UserDetailServiceImpl;
import com.martingago.words.utils.documentation.ApiErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación",
        description = "Operaciones relacionadas con la autenticación de usuarios.")
public class AuthenticationController {

    private final UserDetailServiceImpl userDetailService;


    /**
     * Realiza la autenticación de un usuario mediante email y contraseña.
     *
     * @param authLoginRequestDTO Objeto que contiene las credenciales del usuario (email y contraseña).
     * @return ApiResponseDTO que contiene un AuthResponseDTO con la información del token JWT y el usuario autenticado.
     */
    @Operation(
            summary = "Login de usuario",
            description = "Autentica un usuario utilizando su email y contraseña, devolviendo un token JWT válido para acceso a los " +
                    "recursos protegidos y los detalles básicos del usuario autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAuthenticacionApiResponseExample.class))
            ),
            @ApiResponse(responseCode = "400",
                    description = "Solicitud incorrecta. Campos faltantes o mal formateados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WordErrorApiResponseExample.class),
                            examples = @ExampleObject(
                                    name = "Error 400",
                                    value = ApiErrorExamples.ERROR_400
                            ))
            ),
            @ApiResponse(responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WordErrorApiResponseExample.class),
                            examples = @ExampleObject(
                                    name = "Error 401",
                                    value = ApiErrorExamples.ERROR_401
                            ))),

            @ApiResponse(responseCode = "500",
                    description = "Error interno en el servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WordErrorApiResponseExample.class),
                            examples = @ExampleObject(
                                    name = "Error 500",
                                    value = ApiErrorExamples.ERROR_500
                            ))
            )
    })

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> loginUser(
            @RequestBody @Valid AuthLoginRequestDTO authLoginRequestDTO) {
        AuthResponseDTO authResponseDTO = userDetailService.loginUser(authLoginRequestDTO);
        return ApiResponseDTO.build(
                true,
                "Successfully login",
                HttpStatus.OK.value(),
                authResponseDTO,
                HttpStatus.OK
        );
    }
}
