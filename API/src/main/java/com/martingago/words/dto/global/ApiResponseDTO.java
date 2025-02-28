package com.martingago.words.dto.global;

import com.martingago.words.dto.word.response.WordResponseViewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;

@Getter
@Schema(description = "Empaquetador de respuestas de la API de WordRadar.")
public class ApiResponseDTO<T> {
    @Schema(description = "Indica si la respuesta fue exitosa o no.")
    private boolean status;

    @Schema(description = "Mensaje específico de la solicitud.")
    private String message;

    @Schema(description = "Código de mensaje de la solicitud.")
    private Integer serverCode;

    @Schema(description = "Objecto de respuesta de la petición.")
    private T responseObject;

    @Schema(description = "Momento en el que se ha procesado la petición.")
    private Instant timeStamp;

    @Builder
    protected ApiResponseDTO(boolean status, String message, int serverCode, T responseObject) {
        this.status = status;
        this.message = message;
        this.serverCode = serverCode;
        this.responseObject = responseObject;
        this.timeStamp = Instant.now();
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> build(boolean status, String message, Integer serverCode, T responseObject, HttpStatus httpStatus) {
        ApiResponseDTO<T> response = ApiResponseDTO.<T>builder()
                .status(status)
                .message(message)
                .responseObject(responseObject)
                .serverCode(serverCode)
                .build();

        return new ResponseEntity<>(response, httpStatus);
    }

    public static <T> ResponseEntity<ApiResponseDTO<T>> error(String message, Integer errorCode, HttpStatus status) {
        return build(false, message, errorCode, null, status);
    }
}