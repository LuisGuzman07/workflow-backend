package bo.edu.uagrm.backend.dto;

import bo.edu.uagrm.backend.model.ConexionFlujo;
import bo.edu.uagrm.backend.model.EstadoPolitica;
import bo.edu.uagrm.backend.model.NodoFlujo;
import bo.edu.uagrm.backend.model.PoliticaNegocio;

import java.time.LocalDateTime;
import java.util.List;

public class PoliticaNegocioResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private String creadorUsuarioId;
    private List<String> colaboradoresUsuarioIds;
    private EstadoPolitica estado;
    private List<NodoFlujo> nodos;
    private List<ConexionFlujo> conexiones;
    private String diagrama;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PoliticaNegocioResponse() {
    }

    public static PoliticaNegocioResponse fromEntity(PoliticaNegocio politica) {
        PoliticaNegocioResponse response = new PoliticaNegocioResponse();
        response.id = politica.getId();
        response.nombre = politica.getNombre();
        response.descripcion = politica.getDescripcion();
        response.creadorUsuarioId = politica.getCreadorUsuarioId();
        response.colaboradoresUsuarioIds = politica.getColaboradoresUsuarioIds();
        response.estado = politica.getEstado();
        response.nodos = politica.getNodos();
        response.conexiones = politica.getConexiones();
        response.diagrama = politica.getDiagrama();
        response.createdAt = politica.getCreatedAt();
        response.updatedAt = politica.getUpdatedAt();
        return response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
