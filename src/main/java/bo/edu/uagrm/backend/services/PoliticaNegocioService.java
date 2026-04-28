package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.PoliticaNegocioCreateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioEditRequest;
import bo.edu.uagrm.backend.model.ConexionFlujo;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.EstadoPolitica;
import bo.edu.uagrm.backend.model.NodoFlujo;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.model.TipoConexionFlujo;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PoliticaNegocioService {

    private final PoliticaNegocioRepository politicaNegocioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PoliticaNegocioService(PoliticaNegocioRepository politicaNegocioRepository) {
        this.politicaNegocioRepository = politicaNegocioRepository;
    }

    public PoliticaNegocio crear(PoliticaNegocioCreateRequest request) {
        PoliticaNegocio politica = new PoliticaNegocio();
        politica.setNombre(normalizarTexto(request.getNombre()));
        politica.setDescripcion(normalizarTexto(request.getDescripcion()));
        politica.setDiagrama(request.getDiagrama());
        aplicarModeloFlujoDesdeDiagrama(politica, request.getDiagrama());
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
            aplicarModeloFlujoDesdeDiagrama(politica, request.getDiagrama());
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

    private void aplicarModeloFlujoDesdeDiagrama(PoliticaNegocio politica, String diagrama) {
        if (!StringUtils.hasText(diagrama)) {
            return;
        }
        String trimmed = diagrama.trim();
        try {
            Map<String, Object> payload = objectMapper.readValue(diagrama, new TypeReference<>() {});
            
            // Extraer y guardar los areaForms (formularios por área)
            Map<String, Object> areaForms = (Map<String, Object>) payload.get("areaForms");
            if (areaForms != null && !areaForms.isEmpty()) {
                politica.setAreaForms(areaForms);
            }
            
            List<NodoFlujo> nodos = mapearNodos(payload);
            if (nodos.isEmpty()) {
                nodos = mapearNodosDesdeLanes(payload);
            }
            List<ConexionFlujo> conexiones = mapearConexiones(payload);
            if (nodos.isEmpty()) {
                throw new IllegalArgumentException("El flujo debe contener al menos un nodo.");
            }
            politica.setNodos(nodos);
            politica.setConexiones(conexiones);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            // Compatibilidad con XML legado.
            if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                return;
            }
            throw new IllegalArgumentException("El diagrama de la política tiene formato JSON inválido.");
        }
    }

    private List<NodoFlujo> mapearNodos(Map<String, Object> payload) {
        List<NodoFlujo> nodos = new ArrayList<>();
        List<?> rawNodes = (List<?>) payload.getOrDefault("nodes", List.of());
        for (Object item : rawNodes) {
            if (!(item instanceof LinkedHashMap<?, ?> raw)) {
                continue;
            }
            String id = texto(raw.get("id"));
            String nombre = texto(raw.get("label"));
            String descripcion = texto(raw.get("subLabel"));
            String areaId = texto(raw.get("laneId"));

            if (!StringUtils.hasText(id) || !StringUtils.hasText(nombre) || !StringUtils.hasText(areaId)) {
                continue;
            }

            NodoFlujo nodo = new NodoFlujo();
            nodo.setIdNodo(id);
            nodo.setNombre(nombre);
            nodo.setDescripcion(descripcion);
            nodo.setAreaResponsableId(areaId);
            nodo.setFuncionarioResponsableId("pendiente");
            nodos.add(nodo);
        }
        return nodos;
    }

    private List<ConexionFlujo> mapearConexiones(Map<String, Object> payload) {
        List<ConexionFlujo> conexiones = new ArrayList<>();
        List<?> rawFlows = (List<?>) payload.getOrDefault("flows", List.of());
        for (Object item : rawFlows) {
            if (!(item instanceof LinkedHashMap<?, ?> raw)) {
                continue;
            }
            String from = texto(raw.get("from"));
            String to = texto(raw.get("to"));
            String label = texto(raw.get("label"));
            if (!StringUtils.hasText(from) || !StringUtils.hasText(to)) {
                continue;
            }
            ConexionFlujo conexion = new ConexionFlujo();
            conexion.setNodoOrigenId(from);
            conexion.setNodoDestinoId(to);
            conexion.setTipo(StringUtils.hasText(label) ? TipoConexionFlujo.CONDICIONAL : TipoConexionFlujo.SECUENCIAL);
            conexion.setCondicion(label);
            conexiones.add(conexion);
        }
        return conexiones;
    }

    private List<NodoFlujo> mapearNodosDesdeLanes(Map<String, Object> payload) {
        List<NodoFlujo> nodos = new ArrayList<>();
        List<?> rawLanes = (List<?>) payload.getOrDefault("lanes", List.of());
        int idx = 0;
        for (Object item : rawLanes) {
            if (!(item instanceof LinkedHashMap<?, ?> raw)) {
                continue;
            }
            String laneId = texto(raw.get("id"));
            String laneTitle = texto(raw.get("title"));
            if (!StringUtils.hasText(laneId)) {
                continue;
            }
            idx++;
            NodoFlujo nodo = new NodoFlujo();
            nodo.setIdNodo("lane_node_" + idx);
            nodo.setNombre(StringUtils.hasText(laneTitle) ? laneTitle : "Nodo " + idx);
            nodo.setDescripcion("Nodo generado desde lane");
            nodo.setAreaResponsableId(laneId);
            nodo.setFuncionarioResponsableId("pendiente");
            nodos.add(nodo);
        }
        return nodos;
    }

    private String texto(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }
}
