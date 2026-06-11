package bo.edu.uagrm.backend.dto;

import java.util.List;

public class TramiteDetalleResponse {
    private String id;
    private String codigo;
    private String politicaNombre;
    private String politicaDescripcion;
    private String clienteNombre;
    private String clienteEmail;
    private String estado;
    private String areaActual;
    private int progreso;
    private String fechaInicio;
    private String comentarios;
    private List<PasoDetalle> pasos;
    private List<DocumentoDTO> documentos;

    public static class PasoDetalle {
        private String nombre;
        private String responsable;
        private boolean completado;
        private String fecha; // yyyy-MM-dd HH:mm
        private List<CampoRespuesta> respuestas;

        public PasoDetalle() {
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getResponsable() {
            return responsable;
        }

        public void setResponsable(String responsable) {
            this.responsable = responsable;
        }

        public boolean isCompletado() {
            return completado;
        }

        public void setCompletado(boolean completado) {
            this.completado = completado;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public List<CampoRespuesta> getRespuestas() {
            return respuestas;
        }

        public void setRespuestas(List<CampoRespuesta> respuestas) {
            this.respuestas = respuestas;
        }
    }

    public static class CampoRespuesta {
        private String campoId;
        private String etiqueta;
        private Object valor;

        public CampoRespuesta() {
        }

        public CampoRespuesta(String campoId, String etiqueta, Object valor) {
            this.campoId = campoId;
            this.etiqueta = etiqueta;
            this.valor = valor;
        }

        public String getCampoId() {
            return campoId;
        }

        public void setCampoId(String campoId) {
            this.campoId = campoId;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public void setEtiqueta(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }
    }

    public static class DocumentoDTO {
        private String id;
        private String nombre;
        private String url;
        private String tipo;
        private String fechaSubida;

        public DocumentoDTO() {
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getFechaSubida() {
            return fechaSubida;
        }

        public void setFechaSubida(String fechaSubida) {
            this.fechaSubida = fechaSubida;
        }
    }

    public TramiteDetalleResponse() {
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

    public String getPoliticaDescripcion() {
        return politicaDescripcion;
    }

    public void setPoliticaDescripcion(String politicaDescripcion) {
        this.politicaDescripcion = politicaDescripcion;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
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

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public List<PasoDetalle> getPasos() {
        return pasos;
    }

    public void setPasos(List<PasoDetalle> pasos) {
        this.pasos = pasos;
    }

    public List<DocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoDTO> documentos) {
        this.documentos = documentos;
    }
}
