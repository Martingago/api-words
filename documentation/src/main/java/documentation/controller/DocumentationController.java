package documentation.controller;

import documentation.service.DocumentationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DocumentationController {

    private final DocumentationService documentationService;

    public DocumentationController(DocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @GetMapping("/doc")
    public String documentationPage() {
        return "redoc"; // Referencia a un template HTML
    }

    @GetMapping("/api-docs")
    @ResponseBody
    public ResponseEntity<String> getApiDocs() {
        String openApiSpec = documentationService.getOpenApiSpec();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(openApiSpec);
    }
}