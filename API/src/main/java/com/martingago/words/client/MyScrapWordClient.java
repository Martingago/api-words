package com.martingago.words.client;

import com.martingago.words.dto.word.ScrapWordDTO;
import com.martingago.words.dto.word.WordResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("scraping-microservice")
public interface MyScrapWordClient {

    @PostMapping("/procesar-palabra")
    WordResponseDTO procesarPalabra(@RequestBody ScrapWordDTO scrapWordDTO);
}
