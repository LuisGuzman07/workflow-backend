package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol guardar(Rol rol) {
        String nombreNormalizado = normalizarTexto(rol.getNombre());
        if (rolRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe un rol con el mismo nombre");
        }

        rol.setNombre(nombreNormalizado);
        rol.setDescripcion(normalizarTexto(rol.getDescripcion()));
        return rolRepository.save(rol);
    }

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol buscarPorId(String id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
    }

    public Rol actualizar(String id, Rol rol) {
        Rol existente = buscarPorId(id);
        String nombreNormalizado = normalizarTexto(rol.getNombre());

        if (!existente.getNombre().equalsIgnoreCase(nombreNormalizado)
                && rolRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe un rol con el mismo nombre");
        }

        existente.setNombre(nombreNormalizado);
        existente.setDescripcion(normalizarTexto(rol.getDescripcion()));
        return rolRepository.save(existente);
    }

    public void eliminar(String id) {
        Rol existente = buscarPorId(id);
        rolRepository.delete(existente);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }
}