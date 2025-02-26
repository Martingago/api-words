package com.martingago.words.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Crear el objeto Info primero
        Info info = new Info()
                .title("API WordRadar documentación")
                .description("Documentación de la API REST WordRadar con la información de sus Endpoints")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Soporte de API")
                        .url("http://martingago.dev")
                        .email("soporte@wordradar.es"))
                .license(new License()
                        .name("Licencia Apache 2.0")
                        .url("http://springdoc.org"));

        // Añadir la extensión x-logo
        Map<String, Object> logo = new HashMap<>();
        logo.put("url", "/documentation/wordradar_logo.png");
        logo.put("backgroundColor", "#EB3933");
        logo.put("altText", "WordRadar Logo");
        info.addExtension("x-logo", logo);

        // Construir y devolver el objeto OpenAPI
        return new OpenAPI()
                .info(info)
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación completa")
                        .url("http://martingago.dev/docs"));
    }
}