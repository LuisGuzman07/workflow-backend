package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class PoliticaColaboradoresUpdateRequest {

    @NotBlank(message = "El usuario solicitante es obligatorio")
    private String usuarioSolicitanteId;

    private List<String> colaboradoresUsuarioIds = new ArrayList<>();

    public String getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public void setUsuarioSolicitanteId(String usuarioSolicitanteId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
    }

    public List<String> getColaboradoresUsuarioIds() {
        return colaboradoresUsuarioIds;
    }

    public void setColaboradoresUsuarioIds(List<String> colaboradoresUsuarioIds) {
        this.colaboradoresUsuarioIds = colaboradoresUsuarioIds;
    }
}
