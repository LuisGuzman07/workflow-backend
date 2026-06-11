package bo.edu.uagrm.backend.dto;

import java.util.List;
import java.util.Map;

public class AiGenerateFormResponse {

    private String formName;
    private List<Map<String, Object>> fields;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public List<Map<String, Object>> getFields() {
        return fields;
    }

    public void setFields(List<Map<String, Object>> fields) {
        this.fields = fields;
    }
}
