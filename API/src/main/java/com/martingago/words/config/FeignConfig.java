package com.martingago.words.config;

import com.martingago.words.exceptions.ScrapingErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ScrapingErrorDecoder();
    }
}
