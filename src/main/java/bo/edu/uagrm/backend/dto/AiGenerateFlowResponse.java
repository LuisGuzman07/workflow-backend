package bo.edu.uagrm.backend.dto;

import java.util.Map;

public class AiGenerateFlowResponse {

    private Map<String, Object> draft;
    private String summary;
    private String model;

    public Map<String, Object> getDraft() {
        return draft;
    }

    public void setDraft(Map<String, Object> draft) {
        this.draft = draft;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
