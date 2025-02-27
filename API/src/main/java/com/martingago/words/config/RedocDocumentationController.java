package com.martingago.words.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedocDocumentationConfig {


    @GetMapping("/documentation")
    public String redirectToDocumentation() {
        return "forward:/documentation/index.html";
    }
}
