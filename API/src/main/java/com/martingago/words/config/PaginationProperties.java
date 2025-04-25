package com.martingago.words.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wordradar.pagination")
/**
 * Configura los límites de paginación de la API
 */
public class PaginationProperties {
    private int maxBasicWords;
    private int maxComplexWords;
}