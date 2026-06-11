package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.UsuarioRequest;
import bo.edu.uagrm.backend.dto.UsuarioUpdateRequest;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.exception.UnauthorizedException;
import bo.edu.uagrm.backend.model.Rol;
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
        String rolId = normalizarId(request.getRolId());
        String areaId = normalizarId(request.getAreaId());

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("Ya existe un usuario con el mismo correo");
        }

        Rol rol = buscarRol(rolId);
        boolean esCliente = esRolCliente(rol);

        validarArea(areaId, esCliente);

        Usuario usuario = new Usuario();
        usuario.setNombre(normalizarTexto(request.getNombre()));
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRolId(rolId);
        usuario.setAreaId(esCliente ? null : areaId);

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
        String rolId = normalizarId(request.getRolId());
        String areaId = normalizarId(request.getAreaId());

        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ConflictException("Ya existe un usuario con el mismo correo");
        }

        Rol rol = buscarRol(rolId);
        boolean esCliente = esRolCliente(rol);

        validarArea(areaId, esCliente);

        existente.setNombre(normalizarTexto(request.getNombre()));
        existente.setCorreo(correo);
        existente.setRolId(rolId);
        existente.setAreaId(esCliente ? null : areaId);

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

    private Rol buscarRol(String rolId) {
        if (!StringUtils.hasText(rolId)) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio");
        }

        return rolRepository.findById(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado para el usuario"));
    }

    private void validarArea(String areaId, boolean esCliente) {
        if (esCliente) {
            return;
        }

        if (!StringUtils.hasText(areaId)) {
            throw new IllegalArgumentException("El area del usuario es obligatoria");
        }

        if (!areaRepository.existsById(areaId)) {
            throw new NotFoundException("Area no encontrada para el usuario");
        }
    }

    private boolean esRolCliente(Rol rol) {
        return StringUtils.hasText(rol.getNombre()) && "cliente".equalsIgnoreCase(rol.getNombre().trim());
    }

    private String normalizarId(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }
}