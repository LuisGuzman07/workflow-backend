package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AiGenerateAllFormsRequest {

    @NotBlank(message = "El nombre de la politica es obligatorio")
    private String policyName;

    private String policyDescription;

    @NotNull(message = "La lista de tareas no puede ser nula")
    private List<AiTaskInput> tasks;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public List<AiTaskInput> getTasks() {
        return tasks;
    }

    public void setTasks(List<AiTaskInput> tasks) {
        this.tasks = tasks;
    }
}
