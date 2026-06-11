package bo.edu.uagrm.backend.dto;

import java.util.List;
import java.util.Map;

public class TareaPendienteResponse {

    private String politicaId;
    private String politicaNombre;
    private String areaId;
    private AreaForm areaForm;
    private String tramiteId;
    private String tramiteCodigo;
    private String clienteNombre;

    public String getTramiteId() {
        return tramiteId;
    }

    public void setTramiteId(String tramiteId) {
        this.tramiteId = tramiteId;
    }

    public String getTramiteCodigo() {
        return tramiteCodigo;
    }

    public void setTramiteCodigo(String tramiteCodigo) {
        this.tramiteCodigo = tramiteCodigo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }


    public static class AreaForm {
        private String laneId;
        private String laneTitle;
        private String formName;
        private List<Map<String, Object>> fields;

        public String getLaneId() {
            return laneId;
        }

        public void setLaneId(String laneId) {
            this.laneId = laneId;
        }

        public String getLaneTitle() {
            return laneTitle;
        }

        public void setLaneTitle(String laneTitle) {
            this.laneTitle = laneTitle;
        }

        public String getFormName() {
            return formName;
        }

        public void setFormName(String formName) {
            this.formName = formName;
        }

        public List<Map<String, Object>> getFields() {
            return fields;
        }

        public void setFields(List<Map<String, Object>> fields) {
            this.fields = fields;
        }
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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public AreaForm getAreaForm() {
        return areaForm;
    }

    public void setAreaForm(AreaForm areaForm) {
        this.areaForm = areaForm;
    }
}
