package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.AiGenerateFlowRequest;
import bo.edu.uagrm.backend.dto.AiGenerateFlowResponse;
import bo.edu.uagrm.backend.dto.AiGenerateFormRequest;
import bo.edu.uagrm.backend.dto.AiGenerateFormResponse;
import bo.edu.uagrm.backend.dto.AiGenerateAllFormsRequest;
import bo.edu.uagrm.backend.services.AiFlowGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiFlowGenerationController {

    private final AiFlowGenerationService aiFlowGenerationService;

    public AiFlowGenerationController(AiFlowGenerationService aiFlowGenerationService) {
        this.aiFlowGenerationService = aiFlowGenerationService;
    }

    @PostMapping("/generate-flow")
    public AiGenerateFlowResponse generateFlow(@Valid @RequestBody AiGenerateFlowRequest request) {
        return aiFlowGenerationService.generateFlow(request);
    }

    @PostMapping("/generate-form")
    public AiGenerateFormResponse generateForm(@Valid @RequestBody AiGenerateFormRequest request) {
        return aiFlowGenerationService.generateForm(request);
    }

    @PostMapping("/generate-all-forms")
    public Map<String, Object> generateAllForms(@Valid @RequestBody AiGenerateAllFormsRequest request) {
        return aiFlowGenerationService.generateAllForms(request);
    }
}
