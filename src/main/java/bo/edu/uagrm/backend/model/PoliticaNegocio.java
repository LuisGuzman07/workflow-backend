package bo.edu.uagrm.backend.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "politicas_negocio")
public class PoliticaNegocio {

    @Id
    private String id;

    @NotBlank(message = "El nombre de la politica es obligatorio")
    @Size(max = 150, message = "El nombre de la politica no puede superar 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion de la politica no puede superar 500 caracteres")
    private String descripcion;

    @Size(max = 255, message = "El creador de la politica no puede superar 255 caracteres")
    private String creadorUsuarioId;

    private List<String> colaboradoresUsuarioIds = new ArrayList<>();

    @NotNull(message = "El estado de la politica es obligatorio")
    private EstadoPolitica estado = EstadoPolitica.EDITAR;

    @Valid
    @NotEmpty(message = "La politica debe tener al menos un nodo")
    private List<NodoFlujo> nodos = new ArrayList<>();

    @Valid
    private List<ConexionFlujo> conexiones = new ArrayList<>();

    @NotBlank(message = "El diagrama BPMN es obligatorio")
    private String diagrama;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
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

    public String getCreadorUsuarioId() {
        return creadorUsuarioId;
    }

    public void setCreadorUsuarioId(String creadorUsuarioId) {
        this.creadorUsuarioId = creadorUsuarioId;
    }

    public List<String> getColaboradoresUsuarioIds() {
        return colaboradoresUsuarioIds;
    }

    public void setColaboradoresUsuarioIds(List<String> colaboradoresUsuarioIds) {
        this.colaboradoresUsuarioIds = colaboradoresUsuarioIds;
    }

    public EstadoPolitica getEstado() {
        return estado;
    }

    public void setEstado(EstadoPolitica estado) {
        this.estado = estado;
    }

    public List<NodoFlujo> getNodos() {
        return nodos;
    }

    public void setNodos(List<NodoFlujo> nodos) {
        this.nodos = nodos;
    }

    public List<ConexionFlujo> getConexiones() {
        return conexiones;
    }

    public void setConexiones(List<ConexionFlujo> conexiones) {
        this.conexiones = conexiones;
    }

    public String getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(String diagrama) {
        this.diagrama = diagrama;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
