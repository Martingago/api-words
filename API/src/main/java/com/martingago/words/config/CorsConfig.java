package com.martingago.words.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configuración para endpoints públicos - Más permisiva
        CorsConfiguration publicConfig = new CorsConfiguration();
        publicConfig.setAllowedOrigins(List.of("*"));
        publicConfig.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        publicConfig.setAllowedHeaders(List.of("Content-Type"));
        publicConfig.setMaxAge(3600L);
        publicConfig.setAllowCredentials(false);

        // Configuración para endpoints privados - Más restrictiva
        CorsConfiguration privateConfig = new CorsConfiguration();
        privateConfig.setAllowedOrigins(List.of("http://localhost:4200"));
        privateConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        privateConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        privateConfig.setExposedHeaders(List.of("Authorization"));
        privateConfig.setMaxAge(3600L);
        privateConfig.setAllowCredentials(true);

        // 🔹 Aplicar configuraciones por ruta - IMPORTANTE: Orden de más específico a menos específico
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/private/**", privateConfig);
        source.registerCorsConfiguration("/api/v1/**", publicConfig);

        return source;
    }
}
