package com.martingago.words.controller.global;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Controller
public class RedocDocumentationController {

    /**
     * Redirige al usuario al path de la documentación
     * @return
     */
    @GetMapping("/documentation")
    public String redirectToDocumentation() {
        return "forward:/documentation/index.html";
    }
}
