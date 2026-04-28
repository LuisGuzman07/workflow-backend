package bo.edu.uagrm.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NodoFlujo {

    @NotBlank(message = "El id del nodo es obligatorio")
    private String idNodo;

    @NotBlank(message = "El nombre del nodo es obligatorio")
    @Size(max = 120, message = "El nombre del nodo no puede superar 120 caracteres")
    private String nombre;

    @Size(max = 300, message = "La descripcion del nodo no puede superar 300 caracteres")
    private String descripcion;

    @NotBlank(message = "El area responsable del nodo es obligatoria")
    private String areaResponsableId;

    @NotBlank(message = "El funcionario responsable del nodo es obligatorio")
    private String funcionarioResponsableId;

    public String getIdNodo() {
        return idNodo;
    }

    public void setIdNodo(String idNodo) {
        this.idNodo = idNodo;
    }

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

    public String getAreaResponsableId() {
        return areaResponsableId;
    }

    public void setAreaResponsableId(String areaResponsableId) {
        this.areaResponsableId = areaResponsableId;
    }

    public String getFuncionarioResponsableId() {
        return funcionarioResponsableId;
    }

    public void setFuncionarioResponsableId(String funcionarioResponsableId) {
        this.funcionarioResponsableId = funcionarioResponsableId;
    }
}
