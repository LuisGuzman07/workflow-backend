package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.AiAreaRequest;
import bo.edu.uagrm.backend.dto.AiGenerateFlowRequest;
import bo.edu.uagrm.backend.dto.AiGenerateFlowResponse;
import bo.edu.uagrm.backend.dto.AiGenerateFormRequest;
import bo.edu.uagrm.backend.dto.AiGenerateFormResponse;
import bo.edu.uagrm.backend.dto.AiGenerateAllFormsRequest;
import bo.edu.uagrm.backend.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;

@Service
public class AiFlowGenerationService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.service.base-url:http://localhost:8001}")
    private String aiServiceBaseUrl;

    @Value("${ai.service.generate-path:/api/v1/ai/generate-flow}")
    private String aiServiceGeneratePath;

    @Value("${ai.service.generate-form-path:/api/v1/ai/generate-form}")
    private String aiServiceGenerateFormPath;

    @Value("${ai.service.generate-all-forms-path:/api/v1/ai/generate-all-forms}")
    private String aiServiceGenerateAllFormsPath;

    public AiGenerateFlowResponse generateFlow(AiGenerateFlowRequest request) {
        validarRequest(request);

        Map<String, Object> payload = buildPayload(request);

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(buildGenerateUrl()))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                throw new AiServiceException(
                        "El servicio de IA respondio con error " + httpResponse.statusCode() + ": " + httpResponse.body()
                );
            }

            return objectMapper.readValue(httpResponse.body(), AiGenerateFlowResponse.class);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new AiServiceException("No se pudo leer la respuesta del servicio de IA", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiServiceException("La llamada al servicio de IA fue interrumpida", ex);
        } catch (Exception ex) {
            throw new AiServiceException("No se pudo completar la generacion del flujo con IA", ex);
        }
    }

    private void validarRequest(AiGenerateFlowRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de generacion es obligatoria");
        }
        if (!StringUtils.hasText(request.getPolicyName())) {
            throw new IllegalArgumentException("El nombre de la politica es obligatorio");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("La descripcion del proceso es obligatoria");
        }
    }

    private String buildGenerateUrl() {
        String base = aiServiceBaseUrl == null ? "" : aiServiceBaseUrl.trim();
        String path = aiServiceGeneratePath == null ? "" : aiServiceGeneratePath.trim();
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    private Map<String, Object> buildPayload(AiGenerateFlowRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("policy_name", request.getPolicyName().trim());
        payload.put("description", request.getDescription().trim());
        payload.put("areas", buildAreasPayload(request.getAreas()));
        return payload;
    }

    private List<Map<String, String>> buildAreasPayload(List<AiAreaRequest> areas) {
        List<Map<String, String>> payload = new ArrayList<>();
        if (areas != null) {
            for (AiAreaRequest area : areas) {
                if (area == null || !StringUtils.hasText(area.getId()) || !StringUtils.hasText(area.getTitle())) {
                    continue;
                }
                Map<String, String> item = new LinkedHashMap<>();
                item.put("id", area.getId().trim());
                item.put("title", area.getTitle().trim());
                payload.add(item);
            }
        }
        return payload;
    }

    public AiGenerateFormResponse generateForm(AiGenerateFormRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de generacion de formulario es obligatoria");
        }
        if (!StringUtils.hasText(request.getNodeId()) || !StringUtils.hasText(request.getNodeLabel()) ||
            !StringUtils.hasText(request.getNodeType()) || !StringUtils.hasText(request.getLaneTitle()) ||
            !StringUtils.hasText(request.getPolicyName())) {
            throw new IllegalArgumentException("nodeId, nodeLabel, nodeType, laneTitle y policyName son campos obligatorios");
        }

        try {
            String payloadJson = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(buildFormUrl()))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                throw new AiServiceException(
                        "El servicio de IA respondio con error " + httpResponse.statusCode() + ": " + httpResponse.body()
                );
            }

            return objectMapper.readValue(httpResponse.body(), AiGenerateFormResponse.class);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new AiServiceException("No se pudo leer la respuesta del servicio de IA", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiServiceException("La llamada al servicio de IA fue interrumpida", ex);
        } catch (Exception exc) {
            throw new AiServiceException("No se pudo completar la generacion del formulario con IA", exc);
        }
    }

    private String buildFormUrl() {
        String base = aiServiceBaseUrl == null ? "" : aiServiceBaseUrl.trim();
        String path = aiServiceGenerateFormPath == null ? "" : aiServiceGenerateFormPath.trim();
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> generateAllForms(AiGenerateAllFormsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de generacion masiva es obligatoria");
        }
        if (!StringUtils.hasText(request.getPolicyName())) {
            throw new IllegalArgumentException("El nombre de la politica es obligatorio");
        }
        if (request.getTasks() == null) {
            throw new IllegalArgumentException("La lista de tareas no puede ser nula");
        }

        try {
            String payloadJson = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(buildAllFormsUrl()))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                throw new AiServiceException(
                        "El servicio de IA respondio con error " + httpResponse.statusCode() + ": " + httpResponse.body()
                );
            }

            return objectMapper.readValue(httpResponse.body(), Map.class);
        } catch (AiServiceException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new AiServiceException("No se pudo leer la respuesta del servicio de IA", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiServiceException("La llamada al servicio de IA fue interrumpida", ex);
        } catch (Exception exc) {
            throw new AiServiceException("No se pudo completar la generacion masiva de formularios con IA", exc);
        }
    }

    private String buildAllFormsUrl() {
        String base = aiServiceBaseUrl == null ? "" : aiServiceBaseUrl.trim();
        String path = aiServiceGenerateAllFormsPath == null ? "" : aiServiceGenerateAllFormsPath.trim();
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }
}
