package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.CompletarTareaRequest;
import bo.edu.uagrm.backend.dto.SeguimientoTramiteResponse;
import bo.edu.uagrm.backend.dto.TareaPendienteResponse;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.model.Rol;
import bo.edu.uagrm.backend.model.TareaCompletada;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.AreaRepository;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import bo.edu.uagrm.backend.repository.RolRepository;
import bo.edu.uagrm.backend.repository.TareaCompletadaRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TareaFuncionarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            AreaRepository areaRepository,
            PoliticaNegocioRepository politicaRepository,
            TareaCompletadaRepository completadaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.areaRepository = areaRepository;
        this.politicaRepository = politicaRepository;
        this.completadaRepository = completadaRepository;
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
        List<PoliticaNegocio> politicas = politicaRepository.findAll();
        List<TareaPendienteResponse> pendientes = new ArrayList<>();

        for (PoliticaNegocio politica : politicas) {
            if (politica.getId() == null) {
                continue;
            }
            List<TareaPendienteResponse.AreaForm> forms = getAreaFormsFromPolitica(politica, areaId);
            for (TareaPendienteResponse.AreaForm areaForm : forms) {
                String nodeId = areaForm.getLaneId();
                boolean completada = false;
                if (nodeId != null && !nodeId.isEmpty()) {
                    completada = completadaRepository.existsByUsuarioIdAndPoliticaIdAndAreaIdAndNodeId(
                            usuarioId,
                            politica.getId(),
                            areaId,
                            nodeId
                    );
                } else {
                    completada = completadaRepository.existsByUsuarioIdAndPoliticaIdAndAreaId(
                            usuarioId,
                            politica.getId(),
                            areaId
                    );
                }

                if (!completada) {
                    TareaPendienteResponse response = new TareaPendienteResponse();
                    response.setPoliticaId(politica.getId());
                    response.setPoliticaNombre(politica.getNombre());
                    response.setAreaId(areaId);
                    response.setAreaForm(areaForm);
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

        boolean yaExiste;
        if (request.getNodeId() != null && !request.getNodeId().isEmpty()) {
            yaExiste = completadaRepository.existsByUsuarioIdAndPoliticaIdAndAreaIdAndNodeId(
                    request.getUsuarioId(),
                    request.getPoliticaId(),
                    request.getAreaId(),
                    request.getNodeId()
            );
        } else {
            yaExiste = completadaRepository.existsByUsuarioIdAndPoliticaIdAndAreaId(
                    request.getUsuarioId(),
                    request.getPoliticaId(),
                    request.getAreaId()
            );
        }
        if (yaExiste) {
            throw new ConflictException("La tarea ya fue completada");
        }

        TareaCompletada tarea = new TareaCompletada();
        tarea.setUsuarioId(request.getUsuarioId());
        tarea.setPoliticaId(request.getPoliticaId());
        tarea.setAreaId(request.getAreaId());
        tarea.setNodeId(request.getNodeId());
        tarea.setRespuesta(request.getRespuesta());
        completadaRepository.save(tarea);
    }

    public List<SeguimientoTramiteResponse> listarSeguimiento(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        validarNoFuncionario(usuario.getRolId());

        List<TareaCompletada> completadas = completadaRepository.findAll();
        List<SeguimientoTramiteResponse> response = new ArrayList<>();

        for (TareaCompletada tarea : completadas) {
            SeguimientoTramiteResponse item = new SeguimientoTramiteResponse();
            item.setTareaId(tarea.getId());
            item.setPoliticaId(tarea.getPoliticaId());
            item.setAreaId(tarea.getAreaId());
            item.setRespuesta(tarea.getRespuesta());
            item.setCompletedAt(tarea.getCreatedAt());

            politicaRepository.findById(tarea.getPoliticaId()).ifPresent(p -> {
                item.setPoliticaNombre(p.getNombre());
                TareaPendienteResponse.AreaForm areaForm = null;
                if (tarea.getNodeId() != null && !tarea.getNodeId().isEmpty()) {
                    areaForm = getSpecificNodeFormFromPolitica(p, tarea.getNodeId(), tarea.getAreaId());
                } else {
                    List<TareaPendienteResponse.AreaForm> forms = getAreaFormsFromPolitica(p, tarea.getAreaId());
                    if (!forms.isEmpty()) {
                        areaForm = forms.get(0);
                    }
                }
                if (areaForm != null) {
                    item.setFormularioNombre(areaForm.getFormName());
                    item.setFormularioCampos(areaForm.getFields());
                }
            });
            usuarioRepository.findById(tarea.getUsuarioId()).ifPresent(u -> {
                item.setFuncionarioId(u.getId());
                item.setFuncionarioNombre(u.getNombre());
                item.setFuncionarioCorreo(u.getCorreo());
            });
            areaRepository.findById(tarea.getAreaId()).ifPresent(a -> item.setAreaNombre(a.getNombre()));

            response.add(item);
        }
        return response;
    }

    public void eliminarCompletada(String usuarioIdSolicitante, String tareaId) {
        Usuario usuario = usuarioRepository.findById(usuarioIdSolicitante)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        validarNoFuncionario(usuario.getRolId());

        TareaCompletada completada = completadaRepository.findById(tareaId)
                .orElseThrow(() -> new NotFoundException("Formulario completado no encontrado"));

        completadaRepository.delete(completada);
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
