package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.UsuarioRequest;
import bo.edu.uagrm.backend.dto.UsuarioUpdateRequest;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.exception.UnauthorizedException;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.AreaRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            AreaRepository areaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.areaRepository = areaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardar(UsuarioRequest request) {
        String correo = normalizarCorreo(request.getCorreo());

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("Ya existe un usuario con el mismo correo");
        }

        validarRelaciones(request.getRolId(), request.getAreaId());

        Usuario usuario = new Usuario();
        usuario.setNombre(normalizarTexto(request.getNombre()));
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRolId(request.getRolId().trim());
        usuario.setAreaId(request.getAreaId().trim());

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public Usuario actualizar(String id, UsuarioUpdateRequest request) {
        Usuario existente = buscarPorId(id);
        String correo = normalizarCorreo(request.getCorreo());

        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ConflictException("Ya existe un usuario con el mismo correo");
        }

        validarRelaciones(request.getRolId(), request.getAreaId());

        existente.setNombre(normalizarTexto(request.getNombre()));
        existente.setCorreo(correo);
        existente.setRolId(request.getRolId().trim());
        existente.setAreaId(request.getAreaId().trim());

        if (StringUtils.hasText(request.getPassword())) {
            existente.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    public void eliminar(String id) {
        Usuario existente = buscarPorId(id);
        usuarioRepository.delete(existente);
    }

    public Usuario autenticar(String correo, String password) {
        String correoNormalizado = normalizarCorreo(correo);
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correoNormalizado)
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        boolean credencialesValidas = passwordEncoder.matches(password, usuario.getPassword());

        if (!credencialesValidas && password.equals(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(password));
            usuarioRepository.save(usuario);
            credencialesValidas = true;
        }

        if (!credencialesValidas) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        return usuario;
    }

    private void validarRelaciones(String rolId, String areaId) {
        if (!rolRepository.existsById(rolId.trim())) {
            throw new NotFoundException("Rol no encontrado para el usuario");
        }
        if (!areaRepository.existsById(areaId.trim())) {
            throw new NotFoundException("Area no encontrada para el usuario");
        }
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }
}