package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class IniciarTramiteRequest {

    @NotBlank(message = "El usuarioId es obligatorio")
    private String usuarioId;

    @NotBlank(message = "El politicaId es obligatorio")
    private String politicaId;

    @NotBlank(message = "El comentario o descripcion inicial es obligatorio")
    private String comentarios;

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

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
