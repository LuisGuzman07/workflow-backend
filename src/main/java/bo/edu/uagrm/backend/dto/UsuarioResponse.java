package bo.edu.uagrm.backend.dto;

import bo.edu.uagrm.backend.model.Usuario;

public class UsuarioResponse {

    private String id;
    private String nombre;
    private String correo;
    private String rolId;
    private String areaId;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.id = usuario.getId();
        response.nombre = usuario.getNombre();
        response.correo = usuario.getCorreo();
        response.rolId = usuario.getRolId();
        response.areaId = usuario.getAreaId();
        return response;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRolId() {
        return rolId;
    }

    public String getAreaId() {
        return areaId;
    }
}
