package com.martingago.words.client;

import com.martingago.words.config.FeignConfig;
import com.martingago.words.dto.microservices.word.external.ExternalBaseWordDTO;
import com.martingago.words.dto.microservices.word.external.WordToScrapDTOExternal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "SCRAPING-MICROSERVICE",
        configuration = FeignConfig.class)
public interface MyScrapWordClient {

    /**
     * Emplea el microservicio para manejar una palabra
     * @param word palabra a procesar
     * @return
     */
    @PostMapping("/procesar-palabra")
    ExternalBaseWordDTO procesarPalabra(@RequestBody WordToScrapDTOExternal word);
}
