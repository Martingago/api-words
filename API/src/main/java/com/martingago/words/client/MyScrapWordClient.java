package com.martingago.words.client;

import com.martingago.words.config.FeignConfig;
import com.martingago.words.dto.word.ScrapWordDTO;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "scraping-microservice",
        url = "http://localhost:8090",
        configuration = FeignConfig.class)
public interface MyScrapWordClient {

    /**
     * Emplea el microservicio para manejar una palabra
     * @param scrapWordDTO
     * @return
     */
    @PostMapping("/procesar-palabra")
    WordResponseDTO procesarPalabra(@RequestBody ScrapWordDTO scrapWordDTO);
}
