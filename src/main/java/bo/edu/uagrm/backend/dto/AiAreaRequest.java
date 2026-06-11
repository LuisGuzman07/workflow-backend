package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AiAreaRequest {

    @NotBlank(message = "El id del area es obligatorio")
    private String id;

    @NotBlank(message = "El titulo del area es obligatorio")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
