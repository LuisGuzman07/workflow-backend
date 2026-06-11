package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class CompletarTareaRequest {

    @NotBlank(message = "El usuarioId es obligatorio")
    private String usuarioId;

    @NotBlank(message = "El politicaId es obligatorio")
    private String politicaId;

    @NotBlank(message = "El areaId es obligatorio")
    private String areaId;

    private String nodeId;

    @NotNull(message = "La respuesta es obligatoria")
    private Map<String, Object> respuesta;

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getPoliticaId() {
        return politicaId;
    }

    public void setPoliticaId(String politicaId) {
        this.politicaId = politicaId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Map<String, Object> getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Map<String, Object> respuesta) {
        this.respuesta = respuesta;
    }
}
