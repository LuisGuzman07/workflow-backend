package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PoliticaNegocioCreateRequest {

    @NotBlank(message = "El nombre de la politica es obligatorio")
    @Size(max = 150, message = "El nombre de la politica no puede superar 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion de la politica no puede superar 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El diagrama BPMN es obligatorio")
    private String diagrama;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(String diagrama) {
        this.diagrama = diagrama;
    }
}
