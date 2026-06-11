package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.CompletarTareaRequest;
import bo.edu.uagrm.backend.dto.TareaPendienteResponse;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.model.TareaCompletada;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.model.Tramite;
import bo.edu.uagrm.backend.model.ConexionFlujo;
import bo.edu.uagrm.backend.repository.AreaRepository;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.TareaCompletadaRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import bo.edu.uagrm.backend.repository.TramiteRepository;
import bo.edu.uagrm.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TareaFuncionarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;
    private final PoliticaNegocioRepository politicaRepository;
    private final TareaCompletadaRepository completadaRepository;
    private final TramiteRepository tramiteRepository;
    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TareaFuncionarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            AreaRepository areaRepository,
            PoliticaNegocioRepository politicaRepository,
            TareaCompletadaRepository completadaRepository,
            TramiteRepository tramiteRepository,
            ClienteRepository clienteRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.areaRepository = areaRepository;
        this.politicaRepository = politicaRepository;
        this.completadaRepository = completadaRepository;
        this.tramiteRepository = tramiteRepository;
        this.clienteRepository = clienteRepository;
    }


    public Optional<TareaPendienteResponse> obtenerPendiente(String usuarioId) {
        List<TareaPendienteResponse> pendientes = listarPendientes(usuarioId);
        if (pendientes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pendientes.get(0));
    }

    public List<TareaPendienteResponse> listarPendientes(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (!esFuncionario(usuario.getRolId())) {
            return List.of();
        }

        String areaId = usuario.getAreaId();
        List<Tramite> tramitesActivos = tramiteRepository.findByEstado("En curso");
        List<TareaPendienteResponse> pendientes = new ArrayList<>();

        for (Tramite tramite : tramitesActivos) {
            Optional<PoliticaNegocio> politicaOpt = politicaRepository.findById(tramite.getPoliticaId());
            if (politicaOpt.isEmpty()) {
                continue;
            }
            PoliticaNegocio politica = politicaOpt.get();
            String nodoActualId = tramite.getNodoActualId();
            if (nodoActualId == null || nodoActualId.isEmpty()) {
                continue;
            }

            // Validar si el nodo actual pertenece al area del funcionario
            TareaPendienteResponse.AreaForm areaForm = getSpecificNodeFormFromPolitica(politica, nodoActualId, areaId);
            if (areaForm != null) {
                // Verificar si ya fue completado para esta instancia de tramite
                boolean completado = completadaRepository.findAll().stream()
                        .anyMatch(tc -> tramite.getId().equals(tc.getTramiteId()) && nodoActualId.equals(tc.getNodeId()));

                if (!completado) {
                    // Buscar nombre del cliente
                    String clienteNombre = "Cliente Desconocido";
                    Optional<Usuario> clienteUsuarioOpt = usuarioRepository.findById(tramite.getClienteId());
                    if (clienteUsuarioOpt.isPresent()) {
                        clienteNombre = clienteUsuarioOpt.get().getNombre();
                    }

                    TareaPendienteResponse response = new TareaPendienteResponse();
                    response.setPoliticaId(politica.getId());
                    response.setPoliticaNombre(politica.getNombre());
                    response.setAreaId(areaId);
                    response.setAreaForm(areaForm);
                    response.setTramiteId(tramite.getId());
                    response.setTramiteCodigo(tramite.getCodigo());
                    response.setClienteNombre(clienteNombre);
                    pendientes.add(response);
                }
            }
        }

        return pendientes;
    }

    public void completar(CompletarTareaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        validarRolFuncionario(usuario.getRolId());

        if (!usuario.getAreaId().equals(request.getAreaId())) {
            throw new ConflictException("El usuario solo puede completar tareas de su propia area");
        }

        Tramite tramite = tramiteRepository.findById(request.getTramiteId())
                .orElseThrow(() -> new NotFoundException("Tramite no encontrado para la tarea"));

        PoliticaNegocio politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new NotFoundException("Politica no encontrada"));

        TareaPendienteResponse.AreaForm areaForm;
        if (request.getNodeId() != null && !request.getNodeId().isEmpty()) {
            areaForm = getSpecificNodeFormFromPolitica(politica, request.getNodeId(), request.getAreaId());
        } else {
            List<TareaPendienteResponse.AreaForm> forms = getAreaFormsFromPolitica(politica, request.getAreaId());
            areaForm = forms.isEmpty() ? null : forms.get(0);
        }

        if (areaForm == null) {
            throw new NotFoundException("No existe formulario asignado para la tarea y area especificada");
        }

        boolean yaExiste = completadaRepository.findAll().stream()
                .anyMatch(tc -> request.getTramiteId().equals(tc.getTramiteId()) && request.getNodeId().equals(tc.getNodeId()));

        if (yaExiste) {
            throw new ConflictException("La tarea ya fue completada");
        }

        TareaCompletada tarea = new TareaCompletada();
        tarea.setUsuarioId(request.getUsuarioId());
        tarea.setPoliticaId(request.getPoliticaId());
        tarea.setAreaId(request.getAreaId());
        tarea.setNodeId(request.getNodeId());
        tarea.setTramiteId(request.getTramiteId());
        tarea.setRespuesta(request.getRespuesta());
        completadaRepository.save(tarea);

        // Avanzar el tramite al siguiente nodo del flujo
        List<ConexionFlujo> conexionesSalientes = new ArrayList<>();
        for (ConexionFlujo conn : politica.getConexiones()) {
            if (conn.getNodoOrigenId().equals(request.getNodeId())) {
                conexionesSalientes.add(conn);
            }
        }

        if (conexionesSalientes.isEmpty()) {
            // No hay salida, terminar
            tramite.setEstado("Completado");
            tramite.setNodoActualId("end");
        } else if (conexionesSalientes.size() == 1) {
            // Transicion lineal secuencial
            String nextNodeId = conexionesSalientes.get(0).getNodoDestinoId();
            if (checkNodeIsEnd(politica, nextNodeId)) {
                tramite.setEstado("Completado");
                tramite.setNodoActualId("end");
            } else {
                tramite.setNodoActualId(nextNodeId);
            }
        } else {
            // Gateway / Decision con multiples caminos
            String selectedNextNodeId = null;
            String decisionValue = null;
            
            // Buscar si la respuesta contiene un valor "Si" o "No"
            for (Object val : request.getRespuesta().values()) {
                if (val != null) {
                    String s = val.toString().trim();
                    if ("Si".equalsIgnoreCase(s) || "No".equalsIgnoreCase(s)) {
                        decisionValue = s;
                        break;
                    }
                }
            }

            // Fallback si no es Si/No explicito
            if (decisionValue == null && !request.getRespuesta().isEmpty()) {
                decisionValue = request.getRespuesta().values().iterator().next().toString().trim();
            }

            if (decisionValue != null) {
                for (ConexionFlujo conn : conexionesSalientes) {
                    if (decisionValue.equalsIgnoreCase(conn.getCondicion())) {
                        selectedNextNodeId = conn.getNodoDestinoId();
                        break;
                    }
                }
            }

            // Fallback al primer camino si nada coincide
            if (selectedNextNodeId == null) {
                selectedNextNodeId = conexionesSalientes.get(0).getNodoDestinoId();
            }

            if (checkNodeIsEnd(politica, selectedNextNodeId)) {
                tramite.setEstado("Completado");
                tramite.setNodoActualId("end");
            } else {
                tramite.setNodoActualId(selectedNextNodeId);
            }
        }

        tramite.setUpdatedAt(LocalDateTime.now());
        tramiteRepository.save(tramite);
    }

    private boolean checkNodeIsEnd(PoliticaNegocio politica, String nodeId) {
        if (politica.getDiagrama() == null) {
            return false;
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(politica.getDiagrama(), new TypeReference<>() {});
            List<?> nodes = (List<?>) payload.get("nodes");
            if (nodes != null) {
                for (Object item : nodes) {
                    if (item instanceof Map<?, ?> nodeMap) {
                        String id = (String) nodeMap.get("id");
                        String type = (String) nodeMap.get("type");
                        if (nodeId.equals(id)) {
                            return "end".equalsIgnoreCase(type);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    private List<TareaPendienteResponse.AreaForm> getAreaFormsFromPolitica(PoliticaNegocio politica, String areaId) {
        Map<String, Object> areaForms = politica.getAreaForms();
        if (areaForms == null && politica.getDiagrama() != null) {
            try {
                Map<String, Object> payload = objectMapper.readValue(politica.getDiagrama(), new TypeReference<>() {});
                Object rawAreaForms = payload.get("areaForms");
                if (rawAreaForms instanceof Map<?, ?> rawMap) {
                    areaForms = (Map<String, Object>) rawMap;
                }
            } catch (Exception ignored) {
            }
        }

        List<TareaPendienteResponse.AreaForm> results = new ArrayList<>();
        if (areaForms == null) {
            return results;
        }

        for (Map.Entry<String, Object> entry : areaForms.entrySet()) {
            String nodeId = entry.getKey();
            Object raw = entry.getValue();
            if (raw instanceof Map<?, ?> formMap) {
                String formAreaId = stringValue(formMap.get("laneId"));
                if (areaId.equals(formAreaId)) {
                    TareaPendienteResponse.AreaForm form = new TareaPendienteResponse.AreaForm();
                    form.setLaneId(nodeId);
                    form.setLaneTitle(stringValue(formMap.get("laneTitle")));
                    form.setFormName(stringValue(formMap.get("formName")));

                    Object fieldsRaw = formMap.get("fields");
                    List<Map<String, Object>> fields = new ArrayList<>();
                    if (fieldsRaw instanceof List<?> list) {
                        for (Object item : list) {
                            if (item instanceof Map<?, ?> itemMap) {
                                fields.add((Map<String, Object>) itemMap);
                            }
                        }
                    }
                    form.setFields(fields);
                    results.add(form);
                }
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private TareaPendienteResponse.AreaForm getSpecificNodeFormFromPolitica(PoliticaNegocio politica, String nodeId, String areaId) {
        Map<String, Object> areaForms = politica.getAreaForms();
        if (areaForms == null && politica.getDiagrama() != null) {
            try {
                Map<String, Object> payload = objectMapper.readValue(politica.getDiagrama(), new TypeReference<>() {});
                Object rawAreaForms = payload.get("areaForms");
                if (rawAreaForms instanceof Map<?, ?> rawMap) {
                    areaForms = (Map<String, Object>) rawMap;
                }
            } catch (Exception ignored) {
            }
        }

        if (areaForms == null) {
            return null;
        }

        Object raw = areaForms.get(nodeId);
        if (!(raw instanceof Map<?, ?> formMap)) {
            return null;
        }

        String formAreaId = stringValue(formMap.get("laneId"));
        if (areaId != null && !areaId.equals(formAreaId)) {
            return null;
        }

        TareaPendienteResponse.AreaForm form = new TareaPendienteResponse.AreaForm();
        form.setLaneId(nodeId);
        form.setLaneTitle(stringValue(formMap.get("laneTitle")));
        form.setFormName(stringValue(formMap.get("formName")));

        Object fieldsRaw = formMap.get("fields");
        List<Map<String, Object>> fields = new ArrayList<>();
        if (fieldsRaw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> itemMap) {
                    fields.add((Map<String, Object>) itemMap);
                }
            }
        }
        form.setFields(fields);
        return form;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private void validarRolFuncionario(String rolId) {
        if (!esFuncionario(rolId)) {
            throw new ConflictException("Solo usuarios con rol funcionario pueden completar formularios de tarea");
        }
    }

    private boolean esFuncionario(String rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado para el usuario"));
        String nombreRol = rol.getNombre() == null ? "" : rol.getNombre().trim().toLowerCase();
        return nombreRol.contains("funcionario");
    }

    private void validarNoFuncionario(String rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado para el usuario"));
        String nombreRol = rol.getNombre() == null ? "" : rol.getNombre().trim().toLowerCase();
        if (nombreRol.contains("funcionario")) {
            throw new ConflictException("El funcionario no tiene acceso a seguimiento de tramites");
        }
    }
}
