package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.UsuarioRequest;
import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.dto.UsuarioUpdateRequest;
import bo.edu.uagrm.backend.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import bo.edu.uagrm.backend.services.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.fromEntity(usuario));
    }

    @GetMapping
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listar().stream().map(UsuarioResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtenerUsuarioPorId(@PathVariable String id) {
        return UsuarioResponse.fromEntity(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizarUsuario(@PathVariable String id, @Valid @RequestBody UsuarioUpdateRequest request) {
        return UsuarioResponse.fromEntity(usuarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}