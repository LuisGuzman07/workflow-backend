package bo.edu.uagrm.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class AiGenerateFlowRequest {

    @NotBlank(message = "El nombre de la politica es obligatorio")
    @Size(max = 150, message = "El nombre de la politica no puede superar 150 caracteres")
    private String policyName;

    @NotBlank(message = "La descripcion del proceso es obligatoria")
    @Size(max = 4000, message = "La descripcion del proceso no puede superar 4000 caracteres")
    private String description;

    @Valid
    private List<AiAreaRequest> areas = new ArrayList<>();

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AiAreaRequest> getAreas() {
        return areas;
    }

    public void setAreas(List<AiAreaRequest> areas) {
        this.areas = areas;
    }
}
