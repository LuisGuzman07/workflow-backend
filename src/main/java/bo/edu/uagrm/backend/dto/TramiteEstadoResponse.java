package bo.edu.uagrm.backend.dto;

import java.util.List;

public class TramiteEstadoResponse {
    private String id;
    private String codigo;
    private String tipo; // Nombre de la politica
    private String fechaInicio; // yyyy-MM-dd
    private String estado; // "En curso", "Completado"
    private String areaActual;
    private int progreso;
    private List<Paso> pasos;

    public static class Paso {
        private String nombre;
        private String fecha; // yyyy-MM-dd HH:mm (o null)
        private boolean completado;
        private String responsable;

        public Paso() {
        }

        public Paso(String nombre, String fecha, boolean completado, String responsable) {
            this.nombre = nombre;
            this.fecha = fecha;
            this.completado = completado;
            this.responsable = responsable;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public boolean isCompletado() {
            return completado;
        }

        public void setCompletado(boolean completado) {
            this.completado = completado;
        }

        public String getResponsable() {
            return responsable;
        }

        public void setResponsable(String responsable) {
            this.responsable = responsable;
        }
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
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

    public List<Paso> getPasos() {
        return pasos;
    }

    public void setPasos(List<Paso> pasos) {
        this.pasos = pasos;
    }
}
