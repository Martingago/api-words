package com.martingago.words.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ScrapingErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            // Lee el cuerpo de la respuesta de error
            String errorResponse = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            JsonNode errorNode = objectMapper.readTree(errorResponse);

            // Extrae el mensaje de error del campo 'detail'
            String errorMessage = errorNode.has("detail") ?
                    errorNode.get("detail").asText() :
                    "Error en el servicio de scraping";

            return new ScrapingServiceException(errorMessage, response.status());

        } catch (IOException e) {
            return new Exception("Error al procesar la respuesta del servicio de scraping");
        }
    }
}
