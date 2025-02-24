package com.martingago.words.client;

import com.martingago.words.config.FeignConfig;
import com.martingago.words.dto.word.request.ScrapWordRequestDTO;
import com.martingago.words.dto.word.request.BaseWordRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "SCRAPING-MICROSERVICE",
        configuration = FeignConfig.class)
public interface MyScrapWordClient {

    /**
     * Emplea el microservicio para manejar una palabra
     * @param scrapWordRequestDTO
     * @return
     */
    @PostMapping("/procesar-palabra")
    BaseWordRequestDTO procesarPalabra(@RequestBody ScrapWordRequestDTO scrapWordRequestDTO);
}
