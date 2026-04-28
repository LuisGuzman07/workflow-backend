package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.LoginRequest;
import bo.edu.uagrm.backend.dto.LoginResponse;
import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.getCorreo(), request.getPassword());
        return new LoginResponse("Login exitoso", UsuarioResponse.fromEntity(usuario));
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("mensaje", "Logout exitoso");
    }
}
