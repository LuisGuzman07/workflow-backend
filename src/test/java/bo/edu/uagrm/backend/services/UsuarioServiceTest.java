package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.UsuarioRequest;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.AreaRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void guardarPermiteAreaVaciaCuandoElRolEsCliente() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Cliente Uno");
        request.setCorreo("cliente@correo.com");
        request.setPassword("secreta1");
        request.setRolId("rol-cliente");
        request.setAreaId("   ");

        Rol rol = new Rol();
        rol.setNombre("Cliente");

        when(usuarioRepository.existsByCorreoIgnoreCase("cliente@correo.com")).thenReturn(false);
        when(rolRepository.findById("rol-cliente")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("secreta1")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario guardado = usuarioService.guardar(request);

        assertEquals("rol-cliente", guardado.getRolId());
        assertEquals("cliente@correo.com", guardado.getCorreo());
        assertNull(guardado.getAreaId());
    }
}