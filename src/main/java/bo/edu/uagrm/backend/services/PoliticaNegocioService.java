package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.PoliticaNegocioCreateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioEditRequest;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.EstadoPolitica;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PoliticaNegocioService {

    private final PoliticaNegocioRepository politicaNegocioRepository;

    public PoliticaNegocioService(PoliticaNegocioRepository politicaNegocioRepository) {
        this.politicaNegocioRepository = politicaNegocioRepository;
    }

    public PoliticaNegocio crear(PoliticaNegocioCreateRequest request) {
        PoliticaNegocio politica = new PoliticaNegocio();
        politica.setNombre(normalizarTexto(request.getNombre()));
        politica.setDescripcion(normalizarTexto(request.getDescripcion()));
        politica.setDiagrama(request.getDiagrama());
        politica.setCreadorUsuarioId("system"); // Usar un usuario por defecto
        politica.setEstado(EstadoPolitica.EDITAR);

        return politicaNegocioRepository.save(politica);
    }

    public PoliticaNegocio editar(String id, PoliticaNegocioEditRequest request) {
        PoliticaNegocio politica = obtenerPorId(id);

        if (StringUtils.hasText(request.getNombre())) {
            politica.setNombre(normalizarTexto(request.getNombre()));
        }

        if (StringUtils.hasText(request.getDescripcion())) {
            politica.setDescripcion(normalizarTexto(request.getDescripcion()));
        }

        if (StringUtils.hasText(request.getDiagrama())) {
            politica.setDiagrama(request.getDiagrama());
        }

        return politicaNegocioRepository.save(politica);
    }

    public List<PoliticaNegocio> listar() {
        return politicaNegocioRepository.findAll();
    }

    public PoliticaNegocio obtenerPorId(String id) {
        return politicaNegocioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Politica de negocio no encontrada"));
    }

    public void eliminar(String id) {
        if (!politicaNegocioRepository.existsById(id)) {
            throw new NotFoundException("Politica de negocio no encontrada");
        }
        politicaNegocioRepository.deleteById(id);
    }

    public PoliticaNegocio cambiarEstado(String id, EstadoPolitica nuevoEstado) {
        PoliticaNegocio politica = obtenerPorId(id);
        politica.setEstado(nuevoEstado);
        return politicaNegocioRepository.save(politica);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }
}
