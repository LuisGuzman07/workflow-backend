package bo.edu.uagrm.backend.dto;

public class LoginResponse {

    private String mensaje;
    private UsuarioResponse usuario;

    public LoginResponse(String mensaje, UsuarioResponse usuario) {
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }
}
