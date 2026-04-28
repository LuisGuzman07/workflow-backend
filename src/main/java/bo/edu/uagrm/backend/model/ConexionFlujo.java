package bo.edu.uagrm.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConexionFlujo {

    @NotBlank(message = "El nodo origen es obligatorio")
    private String nodoOrigenId;

    @NotBlank(message = "El nodo destino es obligatorio")
    private String nodoDestinoId;

    @NotNull(message = "El tipo de conexion es obligatorio")
    private TipoConexionFlujo tipo;

    @Size(max = 200, message = "La condicion no puede superar 200 caracteres")
    private String condicion;

    public String getNodoOrigenId() {
        return nodoOrigenId;
    }

    public void setNodoOrigenId(String nodoOrigenId) {
        this.nodoOrigenId = nodoOrigenId;
    }

    public String getNodoDestinoId() {
        return nodoDestinoId;
    }

    public void setNodoDestinoId(String nodoDestinoId) {
        this.nodoDestinoId = nodoDestinoId;
    }

    public TipoConexionFlujo getTipo() {
        return tipo;
    }

    public void setTipo(TipoConexionFlujo tipo) {
        this.tipo = tipo;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }
}
