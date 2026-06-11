package bo.edu.uagrm.backend.dto;

import bo.edu.uagrm.backend.model.Cliente;

public class ClienteResponse {

    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String nit;
    private String usuarioId;
    private java.util.List<String> politicaIds;

    public static ClienteResponse fromEntity(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.id = cliente.getId();
        response.nombre = cliente.getNombre();
        response.correo = cliente.getCorreo();
        response.telefono = cliente.getTelefono();
        response.nit = cliente.getNit();
        response.usuarioId = cliente.getUsuarioId();
        response.politicaIds = cliente.getPoliticaIds();
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

    public String getTelefono() {
        return telefono;
    }

    public String getNit() {
        return nit;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public java.util.List<String> getPoliticaIds() {
        return politicaIds;
    }
}
