package bo.edu.uagrm.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class PoliticaColaboradoresResponse {

    private String politicaId;
    private String creadorUsuarioId;
    private List<UsuarioResponse> colaboradores = new ArrayList<>();

    public String getPoliticaId() {
        return politicaId;
    }

    public void setPoliticaId(String politicaId) {
        this.politicaId = politicaId;
    }

    public String getCreadorUsuarioId() {
        return creadorUsuarioId;
    }

    public void setCreadorUsuarioId(String creadorUsuarioId) {
        this.creadorUsuarioId = creadorUsuarioId;
    }

    public List<UsuarioResponse> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<UsuarioResponse> colaboradores) {
        this.colaboradores = colaboradores;
    }
}
