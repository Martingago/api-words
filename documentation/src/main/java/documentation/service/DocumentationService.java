package documentation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class DocumentationService {

    private final RestTemplate restTemplate;
    private final String apiDocsUrl;
    private String cachedOpenApiSpec;
    private LocalDateTime lastFetched;

    public DocumentationService(RestTemplate restTemplate,
                                String apiDocsUrl) {
        this.restTemplate = restTemplate;
        this.apiDocsUrl = apiDocsUrl;
    }

    public String getOpenApiSpec() {
        // Refrescar cache si es necesario
        if (cachedOpenApiSpec == null ||
                lastFetched == null ||
                lastFetched.plusHours(1).isBefore(LocalDateTime.now())) {
            refreshOpenApiSpec();
        }
        return cachedOpenApiSpec;
    }

    private void refreshOpenApiSpec() {
        try {
            String apiSpec = restTemplate.getForObject(apiDocsUrl, String.class);
            if (apiSpec != null) {
                // Aquí puedes modificar el JSON obtenido para añadir tu configuración personalizada
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(apiSpec);

                // Modificar el nodo 'info' para añadir tu configuración
                ((ObjectNode) root.get("info")).put("description",
                        "Documentación personalizada de la API WordRadar");

                // Añadir logo
                ObjectNode logoNode = mapper.createObjectNode();
                logoNode.put("url", "/images/logo.png");
                logoNode.put("backgroundColor", "#FFFFFF");
                logoNode.put("altText", "WordRadar Logo");

                ((ObjectNode) root.get("info")).set("x-logo", logoNode);

                // Guardar en cache
                cachedOpenApiSpec = mapper.writeValueAsString(root);
                lastFetched = LocalDateTime.now();
            }
        } catch (Exception e) {
            // Log error y manejar adecuadamente
            throw new RuntimeException("Error al obtener la especificación OpenAPI", e);
        }
    }
}