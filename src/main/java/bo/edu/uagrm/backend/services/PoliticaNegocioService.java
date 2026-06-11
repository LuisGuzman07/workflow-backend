package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.PoliticaNegocioCreateRequest;
import bo.edu.uagrm.backend.dto.PoliticaColaboradoresResponse;
import bo.edu.uagrm.backend.dto.PoliticaColaboradoresUpdateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioEditRequest;
import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.model.ConexionFlujo;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.exception.UnauthorizedException;
import bo.edu.uagrm.backend.model.EstadoPolitica;
import bo.edu.uagrm.backend.model.NodoFlujo;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.model.TipoConexionFlujo;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PoliticaNegocioService {
    private static final String LEGACY_SYSTEM_OWNER_ID = "system";

    private final PoliticaNegocioRepository politicaNegocioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PoliticaNegocioService(
            PoliticaNegocioRepository politicaNegocioRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository
    ) {
        this.politicaNegocioRepository = politicaNegocioRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public PoliticaNegocio crear(PoliticaNegocioCreateRequest request) {
        Usuario solicitante = buscarAdministrador(request.getUsuarioSolicitanteId());
        PoliticaNegocio politica = new PoliticaNegocio();
        politica.setNombre(normalizarTexto(request.getNombre()));
        politica.setDescripcion(normalizarTexto(request.getDescripcion()));
        politica.setDiagrama(request.getDiagrama());
        aplicarModeloFlujoDesdeDiagrama(politica, request.getDiagrama());
        politica.setCreadorUsuarioId(solicitante.getId());
        politica.setColaboradoresUsuarioIds(new ArrayList<>());
        politica.setEstado(EstadoPolitica.EDITAR);

        return politicaNegocioRepository.save(politica);
    }

    public PoliticaNegocio editar(String id, PoliticaNegocioEditRequest request) {
        PoliticaNegocio politica = obtenerPorId(id);
        politica = validarPermisoEdicion(politica, request.getUsuarioSolicitanteId());

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
        return obtenerPorId(id, null);
    }

    public PoliticaNegocio obtenerPorId(String id, String usuarioSolicitanteId) {
        PoliticaNegocio politica = politicaNegocioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Politica de negocio no encontrada"));

        if (StringUtils.hasText(usuarioSolicitanteId)) {
            Usuario usuario = usuarioRepository.findById(usuarioSolicitanteId.trim()).orElse(null);
            if (esAdministrador(usuario)) {
                politica = adoptarPropietarioLegacySiCorresponde(politica, usuarioSolicitanteId.trim());
            }
        }

        return politica;
    }

    public void eliminar(String id, String usuarioSolicitanteId) {
        PoliticaNegocio politica = obtenerPorId(id);
        politica = validarPermisoPropietario(politica, usuarioSolicitanteId);
        politicaNegocioRepository.deleteById(id);
    }

    public PoliticaNegocio cambiarEstado(String id, EstadoPolitica nuevoEstado) {
        PoliticaNegocio politica = obtenerPorId(id);
        politica.setEstado(nuevoEstado);
        return politicaNegocioRepository.save(politica);
    }

    public PoliticaColaboradoresResponse obtenerColaboradores(String politicaId, String usuarioSolicitanteId) {
        PoliticaNegocio politica = obtenerPorId(politicaId);
        politica = validarPermisoEdicion(politica, usuarioSolicitanteId);
        return construirRespuestaColaboradores(politica);
    }

    public List<UsuarioResponse> buscarAdministradoresDisponibles(String politicaId, String usuarioSolicitanteId, String termino) {
        PoliticaNegocio politica = obtenerPorId(politicaId);
        politica = validarPermisoEdicion(politica, usuarioSolicitanteId);

        String filtro = normalizarTexto(termino);
        Set<String> excluidos = new HashSet<>(politica.getColaboradoresUsuarioIds());
        excluidos.add(politica.getCreadorUsuarioId());

        return usuarioRepository.findAll().stream()
                .filter(usuario -> !excluidos.contains(usuario.getId()))
                .filter(this::esAdministrador)
                .filter(usuario -> coincideBusqueda(usuario, filtro))
                .sorted(Comparator.comparing(Usuario::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public PoliticaColaboradoresResponse actualizarColaboradores(String politicaId, PoliticaColaboradoresUpdateRequest request) {
        PoliticaNegocio politica = obtenerPorId(politicaId);
        politica = validarPermisoPropietario(politica, request.getUsuarioSolicitanteId());

        List<String> nuevosIds = request.getColaboradoresUsuarioIds() == null
                ? List.of()
                : request.getColaboradoresUsuarioIds().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();

        for (String colaboradorId : nuevosIds) {
            if (colaboradorId.equals(politica.getCreadorUsuarioId())) {
                throw new ConflictException("El creador ya tiene acceso y no debe agregarse como colaborador");
            }
            buscarAdministrador(colaboradorId);
        }

        politica.setColaboradoresUsuarioIds(new ArrayList<>(nuevosIds));
        PoliticaNegocio guardada = politicaNegocioRepository.save(politica);
        return construirRespuestaColaboradores(guardada);
    }

    public boolean puedeEditar(String politicaId, String usuarioId) {
        if (!StringUtils.hasText(usuarioId)) {
            return false;
        }

        PoliticaNegocio politica = obtenerPorId(politicaId);
        if (esPropietarioLegacy(politica)) {
            return esAdministrador(usuarioRepository.findById(usuarioId.trim()).orElse(null));
        }
        return tienePermisoEdicion(politica, usuarioId.trim());
    }

    public UsuarioResponse obtenerUsuarioResumen(String usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioResponse::fromEntity)
                .orElse(null);
    }

    private String normalizarTexto(String valor) {
        return StringUtils.hasText(valor) ? valor.trim() : valor;
    }

    private PoliticaColaboradoresResponse construirRespuestaColaboradores(PoliticaNegocio politica) {
        PoliticaColaboradoresResponse response = new PoliticaColaboradoresResponse();
        response.setPoliticaId(politica.getId());
        response.setCreadorUsuarioId(politica.getCreadorUsuarioId());
        response.setColaboradores(
                politica.getColaboradoresUsuarioIds().stream()
                        .map(usuarioRepository::findById)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .map(UsuarioResponse::fromEntity)
                        .sorted(Comparator.comparing(UsuarioResponse::getNombre, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
        return response;
    }

    private PoliticaNegocio validarPermisoEdicion(PoliticaNegocio politica, String usuarioSolicitanteId) {
        String usuarioId = normalizarId(usuarioSolicitanteId);
        buscarAdministrador(usuarioId);
        politica = adoptarPropietarioLegacySiCorresponde(politica, usuarioId);
        if (!tienePermisoEdicion(politica, usuarioId)) {
            throw new UnauthorizedException("No tienes permisos para editar esta politica de negocio");
        }
        return politica;
    }

    private PoliticaNegocio validarPermisoPropietario(PoliticaNegocio politica, String usuarioSolicitanteId) {
        String usuarioId = normalizarId(usuarioSolicitanteId);
        buscarAdministrador(usuarioId);
        politica = adoptarPropietarioLegacySiCorresponde(politica, usuarioId);
        if (!usuarioId.equals(politica.getCreadorUsuarioId())) {
            throw new UnauthorizedException("Solo el creador de la politica puede gestionar colaboradores o eliminarla");
        }
        return politica;
    }

    private boolean tienePermisoEdicion(PoliticaNegocio politica, String usuarioId) {
        return usuarioId.equals(politica.getCreadorUsuarioId())
                || politica.getColaboradoresUsuarioIds().contains(usuarioId);
    }

    private Usuario buscarAdministrador(String usuarioId) {
        String normalized = normalizarId(usuarioId);
        Usuario usuario = usuarioRepository.findById(normalized)
                .orElseThrow(() -> new NotFoundException("Usuario administrador no encontrado"));

        if (!esAdministrador(usuario)) {
            throw new UnauthorizedException("Solo administradores autorizados pueden colaborar en politicas");
        }
        return usuario;
    }

    private boolean esAdministrador(Usuario usuario) {
        if (usuario == null || !StringUtils.hasText(usuario.getRolId())) {
            return false;
        }

        Rol rol = rolRepository.findById(usuario.getRolId())
                .orElse(null);
        if (rol == null || !StringUtils.hasText(rol.getNombre())) {
            return false;
        }

        String nombreRol = rol.getNombre().trim().toLowerCase();
        return "administrador".equals(nombreRol) || "admin".equals(nombreRol);
    }

    private boolean coincideBusqueda(Usuario usuario, String filtro) {
        if (!StringUtils.hasText(filtro)) {
            return true;
        }
        String term = filtro.toLowerCase();
        String nombre = usuario.getNombre() == null ? "" : usuario.getNombre().toLowerCase();
        String correo = usuario.getCorreo() == null ? "" : usuario.getCorreo().toLowerCase();
        return nombre.contains(term) || correo.contains(term);
    }

    private String normalizarId(String valor) {
        if (!StringUtils.hasText(valor)) {
            throw new IllegalArgumentException("El usuario solicitante es obligatorio");
        }
        return valor.trim();
    }

    private PoliticaNegocio adoptarPropietarioLegacySiCorresponde(PoliticaNegocio politica, String usuarioId) {
        if (!esPropietarioLegacy(politica)) {
            return politica;
        }

        politica.setCreadorUsuarioId(usuarioId);
        politica.getColaboradoresUsuarioIds().removeIf(usuarioId::equals);
        return politicaNegocioRepository.save(politica);
    }

    private boolean esPropietarioLegacy(PoliticaNegocio politica) {
        String creador = politica.getCreadorUsuarioId();
        return !StringUtils.hasText(creador) || LEGACY_SYSTEM_OWNER_ID.equalsIgnoreCase(creador.trim());
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
