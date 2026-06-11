package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AiGenerateFormRequest {

    @NotBlank(message = "El nodeId es obligatorio")
    private String nodeId;

    @NotBlank(message = "El nodeLabel es obligatorio")
    private String nodeLabel;

    @NotBlank(message = "El nodeType es obligatorio")
    private String nodeType;

    @NotBlank(message = "El laneTitle es obligatorio")
    private String laneTitle;

    @NotBlank(message = "El policyName es obligatorio")
    private String policyName;

    private String policyDescription;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getLaneTitle() {
        return laneTitle;
    }

    public void setLaneTitle(String laneTitle) {
        this.laneTitle = laneTitle;
    }

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
}
