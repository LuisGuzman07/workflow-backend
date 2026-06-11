package bo.edu.uagrm.backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

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

    public Cliente() {
    }

    public Cliente(String nombre, String correo, String telefono, String nit, String usuarioId, java.util.List<String> politicaIds) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.nit = nit;
        this.usuarioId = usuarioId;
        this.politicaIds = politicaIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
