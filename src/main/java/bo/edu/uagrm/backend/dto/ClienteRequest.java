package bo.edu.uagrm.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 100, message = "El nombre del cliente no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo del cliente es obligatorio")
    @Email(message = "El correo del cliente no tiene un formato valido")
    @Size(max = 150, message = "El correo del cliente no puede superar 150 caracteres")
    private String correo;

    @Size(max = 20, message = "El telefono no puede superar 20 caracteres")
    private String telefono;

    @Size(max = 30, message = "El nit/documento no puede superar 30 caracteres")
    private String nit;

    private String usuarioId;

    private java.util.List<String> politicaIds = new java.util.ArrayList<>();

    private boolean crearUsuarioAsociado;

    @Size(min = 6, max = 120, message = "La contrasena debe tener entre 6 y 120 caracteres")
    private String password;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public java.util.List<String> getPoliticaIds() {
        return politicaIds;
    }

    public void setPoliticaIds(java.util.List<String> politicaIds) {
        this.politicaIds = politicaIds;
    }

    public boolean isCrearUsuarioAsociado() {
        return crearUsuarioAsociado;
    }

    public void setCrearUsuarioAsociado(boolean crearUsuarioAsociado) {
        this.crearUsuarioAsociado = crearUsuarioAsociado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
