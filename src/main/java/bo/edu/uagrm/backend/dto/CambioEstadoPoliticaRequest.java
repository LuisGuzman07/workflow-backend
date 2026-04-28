package bo.edu.uagrm.backend.dto;

import bo.edu.uagrm.backend.model.EstadoPolitica;
import jakarta.validation.constraints.NotNull;

public class CambioEstadoPoliticaRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPolitica estado;

    public EstadoPolitica getEstado() {
        return estado;
    }

    public void setEstado(EstadoPolitica estado) {
        this.estado = estado;
    }
}
