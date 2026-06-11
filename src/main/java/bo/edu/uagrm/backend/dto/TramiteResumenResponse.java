package bo.edu.uagrm.backend.dto;

public class TramiteResumenResponse {
    private String id;
    private String codigo;
    private String politicaNombre;
    private String clienteNombre;
    private String estado;
    private String areaActual;
    private String fechaInicio;

    public TramiteResumenResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPoliticaNombre() {
        return politicaNombre;
    }

    public void setPoliticaNombre(String politicaNombre) {
        this.politicaNombre = politicaNombre;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAreaActual() {
        return areaActual;
    }

    public void setAreaActual(String areaActual) {
        this.areaActual = areaActual;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
}
