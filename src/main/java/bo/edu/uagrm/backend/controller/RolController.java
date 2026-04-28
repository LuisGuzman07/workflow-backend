package bo.edu.uagrm.backend.controller;

import jakarta.validation.Valid;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.services.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@Valid @RequestBody Rol rol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.guardar(rol));
    }

    @GetMapping
    public List<Rol> listarRoles() {
        return rolService.listar();
    }

    @GetMapping("/{id}")
    public Rol obtenerRolPorId(@PathVariable String id) {
        return rolService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Rol actualizarRol(@PathVariable String id, @Valid @RequestBody Rol rol) {
        return rolService.actualizar(id, rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable String id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}