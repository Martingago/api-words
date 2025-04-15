package com.martingago.words.dto.docs;

import com.martingago.words.dto.global.ApiResponseDTO;
import com.martingago.words.dto.global.stats.WordStatsDTO;

/**
 * Clase intermedia que sirve para generar la documentación de Swagger.
 */
public class StatsApiResponseExample extends ApiResponseDTO<WordStatsDTO> {

    protected StatsApiResponseExample(boolean status, String message, int serverCode, WordStatsDTO responseObject) {
        super(status, message, serverCode, responseObject);
    }
}
