package bo.edu.uagrm.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "areas")
public class Area {

    @Id
    private String id;

    @NotBlank(message = "El nombre del area es obligatorio")
    @Size(max = 100, message = "El nombre del area no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 300, message = "La descripcion del area no puede superar 300 caracteres")
    private String descripcion;

    public Area() {
    }

    public Area(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}