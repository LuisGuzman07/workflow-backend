package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.ClienteRequest;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.Cliente;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.ClienteRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cliente guardar(ClienteRequest request) {
        String correo = normalizarCorreo(request.getCorreo());
        
        if (clienteRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("Ya existe un cliente con el mismo correo");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre().trim());
        cliente.setCorreo(correo);
        cliente.setTelefono(normalizarTexto(request.getTelefono()));
        cliente.setNit(normalizarTexto(request.getNit()));
        if (request.getPoliticaIds() != null) {
            cliente.setPoliticaIds(request.getPoliticaIds());
        }

        if (request.isCrearUsuarioAsociado()) {
            if (!StringUtils.hasText(request.getPassword())) {
                throw new IllegalArgumentException("La contrasena es obligatoria para crear un usuario");
            }
            if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
                throw new ConflictException("Ya existe un usuario con el mismo correo");
            }

            Rol rolCliente = obtenerOCrearRolCliente();
            Usuario usuario = new Usuario();
            usuario.setNombre(cliente.getNombre());
            usuario.setCorreo(cliente.getCorreo());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRolId(rolCliente.getId());
            usuario.setAreaId(null);
            
            usuarioRepository.save(usuario);
            cliente.setUsuarioId(usuario.getId());
        } else {
            cliente.setUsuarioId(normalizarTexto(request.getUsuarioId()));
        }

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(String id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    public Cliente actualizar(String id, ClienteRequest request) {
        Cliente existente = buscarPorId(id);
        String correo = normalizarCorreo(request.getCorreo());

        if (clienteRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ConflictException("Ya existe un cliente con el mismo correo");
        }

        existente.setNombre(request.getNombre().trim());
        existente.setCorreo(correo);
        existente.setTelefono(normalizarTexto(request.getTelefono()));
        existente.setNit(normalizarTexto(request.getNit()));
        if (request.getPoliticaIds() != null) {
            existente.setPoliticaIds(request.getPoliticaIds());
        }

        // Sincronizar o crear el usuario asociado
        if (StringUtils.hasText(existente.getUsuarioId())) {
            // Ya tiene un usuario, lo actualizamos
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(existente.getUsuarioId());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, usuario.getId())) {
                    throw new ConflictException("Ya existe un usuario con el mismo correo del cliente");
                }
                usuario.setNombre(existente.getNombre());
                usuario.setCorreo(existente.getCorreo());
                if (StringUtils.hasText(request.getPassword())) {
                    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
                }
                usuarioRepository.save(usuario);
            }
        } else if (request.isCrearUsuarioAsociado()) {
            // No tenía usuario y se solicitó crear uno
            if (!StringUtils.hasText(request.getPassword())) {
                throw new IllegalArgumentException("La contrasena es obligatoria para crear un usuario");
            }
            if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
                throw new ConflictException("Ya existe un usuario con el correo de este cliente");
            }

            Rol rolCliente = obtenerOCrearRolCliente();
            Usuario usuario = new Usuario();
            usuario.setNombre(existente.getNombre());
            usuario.setCorreo(existente.getCorreo());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRolId(rolCliente.getId());
            usuario.setAreaId(null);

            usuarioRepository.save(usuario);
            existente.setUsuarioId(usuario.getId());
        }

        return clienteRepository.save(existente);
    }

    public void eliminar(String id) {
        Cliente existente = buscarPorId(id);
        if (StringUtils.hasText(existente.getUsuarioId())) {
            usuarioRepository.deleteById(existente.getUsuarioId());
        }
        clienteRepository.delete(existente);
    }

    private Rol obtenerOCrearRolCliente() {
        return rolRepository.findByNombreIgnoreCase("cliente")
                .orElseGet(() -> {
                    Rol rol = new Rol("cliente", "Rol asignado a los clientes del sistema");
                    return rolRepository.save(rol);
                });
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : null;
    }
}
