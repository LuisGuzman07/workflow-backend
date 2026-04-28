package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.Area;
import bo.edu.uagrm.backend.repository.AreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public Area guardar(Area area) {
        String nombreNormalizado = normalizarTexto(area.getNombre());
        if (areaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe un area con el mismo nombre");
        }

        area.setNombre(nombreNormalizado);
        area.setDescripcion(normalizarTexto(area.getDescripcion()));
        return areaRepository.save(area);
    }

    public List<Area> listar() {
        return areaRepository.findAll();
    }

    public Area buscarPorId(String id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Area no encontrada"));
    }

    public Area actualizar(String id, Area area) {
        Area existente = buscarPorId(id);
        String nombreNormalizado = normalizarTexto(area.getNombre());

        if (!existente.getNombre().equalsIgnoreCase(nombreNormalizado)
                && areaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe un area con el mismo nombre");
        }

        existente.setNombre(nombreNormalizado);
        existente.setDescripcion(normalizarTexto(area.getDescripcion()));
        return areaRepository.save(existente);
    }

    public void eliminar(String id) {
        Area existente = buscarPorId(id);
        areaRepository.delete(existente);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }
}