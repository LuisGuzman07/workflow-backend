package bo.edu.uagrm.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "tareas_completadas")
public class TareaCompletada {

    @Id
    private String id;
    private String usuarioId;
    private String politicaId;
    private String areaId;
    private String nodeId;
    private String tramiteId;
    private Map<String, Object> respuesta;

    @CreatedDate
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public String getTramiteId() {
        return tramiteId;
    }

    public void setTramiteId(String tramiteId) {
        this.tramiteId = tramiteId;
    }


    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getUsuarioId() {
        return usuarioId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
