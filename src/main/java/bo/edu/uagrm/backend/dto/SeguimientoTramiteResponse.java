package bo.edu.uagrm.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SeguimientoTramiteResponse {

    private String tareaId;
    private String politicaId;
    private String politicaNombre;
    private String funcionarioId;
    private String funcionarioNombre;
    private String funcionarioCorreo;
    private String areaId;
    private String areaNombre;
    private String formularioNombre;
    private List<Map<String, Object>> formularioCampos;
    private Map<String, Object> respuesta;
    private LocalDateTime completedAt;

    public String getTareaId() {
        return tareaId;
    }

    public void setTareaId(String tareaId) {
        this.tareaId = tareaId;
    }

    public String getPoliticaId() {
        return politicaId;
    }

    public void setPoliticaId(String politicaId) {
        this.politicaId = politicaId;
    }

    public String getPoliticaNombre() {
        return politicaNombre;
    }

    public void setPoliticaNombre(String politicaNombre) {
        this.politicaNombre = politicaNombre;
    }

    public String getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(String funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getFuncionarioNombre() {
        return funcionarioNombre;
    }

    public void setFuncionarioNombre(String funcionarioNombre) {
        this.funcionarioNombre = funcionarioNombre;
    }

    public String getFuncionarioCorreo() {
        return funcionarioCorreo;
    }

    public void setFuncionarioCorreo(String funcionarioCorreo) {
        this.funcionarioCorreo = funcionarioCorreo;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaNombre() {
        return areaNombre;
    }

    public void setAreaNombre(String areaNombre) {
        this.areaNombre = areaNombre;
    }

    public String getFormularioNombre() {
        return formularioNombre;
    }

    public void setFormularioNombre(String formularioNombre) {
        this.formularioNombre = formularioNombre;
    }

    public List<Map<String, Object>> getFormularioCampos() {
        return formularioCampos;
    }

    public void setFormularioCampos(List<Map<String, Object>> formularioCampos) {
        this.formularioCampos = formularioCampos;
    }

    public Map<String, Object> getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Map<String, Object> respuesta) {
        this.respuesta = respuesta;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
