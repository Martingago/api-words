package com.martingago.words.dto.global.stats;

import com.martingago.words.dto.global.ApiResponseDTO;

/**
 * Clase intermedia que sirve para generar la documentación de Swagger.
 */
public class StatsApiResponse  extends ApiResponseDTO<WordStatsDTO> {

    protected StatsApiResponse(boolean status, String message, int serverCode, WordStatsDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }
}
