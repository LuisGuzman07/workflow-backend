package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.IniciarTramiteRequest;
import bo.edu.uagrm.backend.dto.TramiteEstadoResponse;
import bo.edu.uagrm.backend.dto.TramiteResumenResponse;
import bo.edu.uagrm.backend.dto.TramiteDetalleResponse;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.Area;
import bo.edu.uagrm.backend.model.Cliente;
import bo.edu.uagrm.backend.model.NodoFlujo;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.model.TareaCompletada;
import bo.edu.uagrm.backend.model.Tramite;
import bo.edu.uagrm.backend.model.DocumentoAdjunto;
import bo.edu.uagrm.backend.model.Usuario;
import bo.edu.uagrm.backend.repository.AreaRepository;
import bo.edu.uagrm.backend.repository.ClienteRepository;
import bo.edu.uagrm.backend.repository.PoliticaNegocioRepository;
import bo.edu.uagrm.backend.repository.TareaCompletadaRepository;
import bo.edu.uagrm.backend.repository.TramiteRepository;
import bo.edu.uagrm.backend.repository.DocumentoAdjuntoRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TramiteService {

    private final TramiteRepository tramiteRepository;
    private final ClienteRepository clienteRepository;
    private final PoliticaNegocioRepository politicaRepository;
    private final TareaCompletadaRepository completadaRepository;
    private final AreaRepository areaRepository;
    private final DocumentoAdjuntoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TramiteService(
            TramiteRepository tramiteRepository,
            ClienteRepository clienteRepository,
            PoliticaNegocioRepository politicaRepository,
            TareaCompletadaRepository completadaRepository,
            AreaRepository areaRepository,
            DocumentoAdjuntoRepository documentoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.tramiteRepository = tramiteRepository;
        this.clienteRepository = clienteRepository;
        this.politicaRepository = politicaRepository;
        this.completadaRepository = completadaRepository;
        this.areaRepository = areaRepository;
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
    }


    public Tramite iniciar(IniciarTramiteRequest request) {
        // Buscar el cliente por su usuarioId (el usuario logueado en la plataforma)
        Cliente cliente = clienteRepository.findByUsuarioId(request.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Cliente asociado no encontrado para el usuario " + request.getUsuarioId()));

        PoliticaNegocio politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new NotFoundException("Politica de negocio no encontrada"));

        // Encontrar el primer nodo del flujo (inmediatamente despues del nodo start)
        String nodoActualId = null;
        if (politica.getDiagrama() != null) {
            try {
                Map<String, Object> payload = objectMapper.readValue(politica.getDiagrama(), new TypeReference<>() {});
                List<?> nodes = (List<?>) payload.get("nodes");
                String startNodeId = null;
                if (nodes != null) {
                    for (Object item : nodes) {
                        if (item instanceof Map<?, ?> nodeMap) {
                            String type = (String) nodeMap.get("type");
                            if ("start".equalsIgnoreCase(type)) {
                                startNodeId = (String) nodeMap.get("id");
                                break;
                            }
                        }
                    }
                }
                if (startNodeId != null) {
                    List<?> flows = (List<?>) payload.get("flows");
                    if (flows != null) {
                        for (Object item : flows) {
                            if (item instanceof Map<?, ?> flowMap) {
                                String from = (String) flowMap.get("from");
                                if (startNodeId.equals(from)) {
                                    nodoActualId = (String) flowMap.get("to");
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback al primer nodo si no se pudo determinar desde el diagrama
        if (nodoActualId == null && !politica.getNodos().isEmpty()) {
            nodoActualId = politica.getNodos().get(0).getIdNodo();
        }

        if (nodoActualId == null) {
            throw new IllegalArgumentException("La politica seleccionada no tiene nodos configurados en su flujo");
        }

        Tramite tramite = new Tramite();
        // Generar codigo unico random T-XXXXXX
        tramite.setCodigo("T-" + (int) (100000 + Math.random() * 900000));
        tramite.setClienteId(request.getUsuarioId()); // guardamos su id de usuario
        tramite.setPoliticaId(request.getPoliticaId());
        tramite.setEstado("En curso");
        tramite.setComentarios(request.getComentarios());
        tramite.setNodoActualId(nodoActualId);
        tramite.setCreatedAt(LocalDateTime.now());
        tramite.setUpdatedAt(LocalDateTime.now());

        return tramiteRepository.save(tramite);
    }

    public List<TramiteEstadoResponse> listarSeguimientoCliente(String usuarioId) {
        List<Tramite> tramites = tramiteRepository.findByClienteId(usuarioId);
        List<TramiteEstadoResponse> responseList = new ArrayList<>();

        for (Tramite t : tramites) {
            Optional<PoliticaNegocio> politicaOpt = politicaRepository.findById(t.getPoliticaId());
            if (politicaOpt.isEmpty()) {
                continue;
            }

            PoliticaNegocio politica = politicaOpt.get();
            TramiteEstadoResponse res = new TramiteEstadoResponse();
            res.setId(t.getId());
            res.setCodigo(t.getCodigo());
            res.setTipo(politica.getNombre());
            res.setFechaInicio(t.getCreatedAt() != null ? t.getCreatedAt().format(dateOnlyFormatter) : "");
            res.setEstado(t.getEstado());

            // Listar los pasos (pasos del diagrama de la politica)
            List<TramiteEstadoResponse.Paso> pasos = new ArrayList<>();
            List<NodoFlujo> nodos = politica.getNodos();
            
            int completedCount = 0;
            String areaActual = "Finalizado";

            for (NodoFlujo nodo : nodos) {
                // Buscar si esta tarea fue completada para este tramite especifico
                List<TareaCompletada> completadas = completadaRepository.findAll();
                Optional<TareaCompletada> tareaCompletadaOpt = completadas.stream()
                        .filter(tc -> t.getId().equals(tc.getTramiteId()) && nodo.getIdNodo().equals(tc.getNodeId()))
                        .findFirst();

                boolean completado = tareaCompletadaOpt.isPresent();
                String fecha = null;
                if (completado) {
                    completedCount++;
                    fecha = tareaCompletadaOpt.get().getCreatedAt() != null 
                            ? tareaCompletadaOpt.get().getCreatedAt().format(formatter) 
                            : LocalDateTime.now().format(formatter);
                }

                String areaNombre = "";
                Optional<Area> areaOpt = areaRepository.findById(nodo.getAreaResponsableId());
                if (areaOpt.isPresent()) {
                    areaNombre = areaOpt.get().getNombre();
                } else {
                    areaNombre = nodo.getAreaResponsableId();
                }

                pasos.add(new TramiteEstadoResponse.Paso(
                        nodo.getNombre(),
                        fecha,
                        completado,
                        areaNombre
                ));

                // Si este nodo es el nodo actual, registrar el area responsable
                if (nodo.getIdNodo().equals(t.getNodoActualId())) {
                    areaActual = areaNombre;
                }
            }

            res.setPasos(pasos);
            res.setAreaActual(t.getEstado().equalsIgnoreCase("Completado") ? "Finalizado" : areaActual);

            // Calcular progreso (del 1 al total de pasos)
            int totalSteps = pasos.size();
            if (t.getEstado().equalsIgnoreCase("Completado")) {
                res.setProgreso(totalSteps);
            } else {
                int currentStepIdx = 0;
                for (int i = 0; i < nodos.size(); i++) {
                    if (nodos.get(i).getIdNodo().equals(t.getNodoActualId())) {
                        currentStepIdx = i;
                        break;
                    }
                }
                res.setProgreso(currentStepIdx + 1);
            }

            responseList.add(res);
        }

        return responseList;
    }

    public List<TramiteResumenResponse> listarTodos() {
        List<Tramite> tramites = tramiteRepository.findAll();
        List<TramiteResumenResponse> list = new ArrayList<>();

        for (Tramite t : tramites) {
            TramiteResumenResponse res = new TramiteResumenResponse();
            res.setId(t.getId());
            res.setCodigo(t.getCodigo());
            res.setEstado(t.getEstado());
            res.setFechaInicio(t.getCreatedAt() != null ? t.getCreatedAt().format(dateOnlyFormatter) : "");

            // Buscar Politica
            politicaRepository.findById(t.getPoliticaId()).ifPresent(p -> res.setPoliticaNombre(p.getNombre()));

            // Buscar Cliente
            usuarioRepository.findById(t.getClienteId()).ifPresent(u -> res.setClienteNombre(u.getNombre()));
            if (res.getClienteNombre() == null) {
                res.setClienteNombre("Cliente Desconocido");
            }

            // Buscar Area Actual
            String areaActual = "Finalizado";
            if (!t.getEstado().equalsIgnoreCase("Completado") && t.getNodoActualId() != null) {
                Optional<PoliticaNegocio> pOpt = politicaRepository.findById(t.getPoliticaId());
                if (pOpt.isPresent()) {
                    for (NodoFlujo nodo : pOpt.get().getNodos()) {
                        if (nodo.getIdNodo().equals(t.getNodoActualId())) {
                            Optional<Area> areaOpt = areaRepository.findById(nodo.getAreaResponsableId());
                            areaActual = areaOpt.map(Area::getNombre).orElse(nodo.getAreaResponsableId());
                            break;
                        }
                    }
                }
            }
            res.setAreaActual(areaActual);
            list.add(res);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public TramiteDetalleResponse obtenerDetalle(String id) {
        Tramite t = tramiteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tramite no encontrado"));

        PoliticaNegocio politica = politicaRepository.findById(t.getPoliticaId())
                .orElseThrow(() -> new NotFoundException("Politica no encontrada"));

        TramiteDetalleResponse res = new TramiteDetalleResponse();
        res.setId(t.getId());
        res.setCodigo(t.getCodigo());
        res.setPoliticaNombre(politica.getNombre());
        res.setPoliticaDescripcion(politica.getDescripcion());
        res.setEstado(t.getEstado());
        res.setFechaInicio(t.getCreatedAt() != null ? t.getCreatedAt().format(dateOnlyFormatter) : "");
        res.setComentarios(t.getComentarios());

        // Buscar Cliente
        usuarioRepository.findById(t.getClienteId()).ifPresent(u -> {
            res.setClienteNombre(u.getNombre());
            res.setClienteEmail(u.getCorreo());
        });

        // Buscar Area Actual
        String areaActual = "Finalizado";
        if (!t.getEstado().equalsIgnoreCase("Completado") && t.getNodoActualId() != null) {
            for (NodoFlujo nodo : politica.getNodos()) {
                if (nodo.getIdNodo().equals(t.getNodoActualId())) {
                    Optional<Area> areaOpt = areaRepository.findById(nodo.getAreaResponsableId());
                    areaActual = areaOpt.map(Area::getNombre).orElse(nodo.getAreaResponsableId());
                    break;
                }
            }
        }
        res.setAreaActual(areaActual);

        // Listar los pasos con sus respuestas completadas
        List<TramiteDetalleResponse.PasoDetalle> pasos = new ArrayList<>();

        for (NodoFlujo nodo : politica.getNodos()) {
            List<TareaCompletada> completadas = completadaRepository.findAll();
            Optional<TareaCompletada> tareaCompletadaOpt = completadas.stream()
                    .filter(tc -> t.getId().equals(tc.getTramiteId()) && nodo.getIdNodo().equals(tc.getNodeId()))
                    .findFirst();

            boolean completado = tareaCompletadaOpt.isPresent();
            String fecha = null;
            List<TramiteDetalleResponse.CampoRespuesta> respuestasDTO = new ArrayList<>();

            if (completado) {
                TareaCompletada tc = tareaCompletadaOpt.get();
                fecha = tc.getCreatedAt() != null ? tc.getCreatedAt().format(formatter) : LocalDateTime.now().format(formatter);

                // Mapear cada respuesta a su etiqueta visible buscando en el formulario de la politica
                Map<String, Object> formMap = null;
                if (politica.getAreaForms() != null) {
                    formMap = (Map<String, Object>) politica.getAreaForms().get(nodo.getIdNodo());
                }
                
                List<?> fieldsList = null;
                if (formMap != null) {
                    fieldsList = (List<?>) formMap.get("fields");
                }

                if (tc.getRespuesta() != null) {
                    for (Map.Entry<String, Object> entry : tc.getRespuesta().entrySet()) {
                        String fieldId = entry.getKey();
                        Object val = entry.getValue();
                        String label = fieldId; // fallback

                        // Buscar etiqueta en fieldsList
                        if (fieldsList != null) {
                            for (Object fieldObj : fieldsList) {
                                if (fieldObj instanceof Map<?, ?> fieldMap) {
                                    if (fieldId.equals(fieldMap.get("id"))) {
                                        label = (String) fieldMap.get("label");
                                        break;
                                    }
                                }
                            }
                        }
                        respuestasDTO.add(new TramiteDetalleResponse.CampoRespuesta(fieldId, label, val));
                    }
                }
            }

            String areaNombre = areaRepository.findById(nodo.getAreaResponsableId())
                    .map(Area::getNombre)
                    .orElse(nodo.getAreaResponsableId());

            TramiteDetalleResponse.PasoDetalle paso = new TramiteDetalleResponse.PasoDetalle();
            paso.setNombre(nodo.getNombre());
            paso.setResponsable(areaNombre);
            paso.setCompletado(completado);
            paso.setFecha(fecha);
            paso.setRespuestas(respuestasDTO);

            pasos.add(paso);
        }
        res.setPasos(pasos);

        // Calcular progreso
        if (t.getEstado().equalsIgnoreCase("Completado")) {
            res.setProgreso(pasos.size());
        } else {
            int currentStepIdx = 0;
            for (int i = 0; i < politica.getNodos().size(); i++) {
                if (politica.getNodos().get(i).getIdNodo().equals(t.getNodoActualId())) {
                    currentStepIdx = i;
                    break;
                }
            }
            res.setProgreso(currentStepIdx + 1);
        }

        // Buscar Documentos
        List<DocumentoAdjunto> docs = documentoRepository.findByTramiteId(id);
        List<TramiteDetalleResponse.DocumentoDTO> documentosDTO = new ArrayList<>();
        for (DocumentoAdjunto doc : docs) {
            TramiteDetalleResponse.DocumentoDTO docDTO = new TramiteDetalleResponse.DocumentoDTO();
            docDTO.setId(doc.getId());
            docDTO.setNombre(doc.getNombre());
            docDTO.setUrl(doc.getUrl());
            docDTO.setTipo(doc.getTipo());
            docDTO.setFechaSubida(doc.getFechaSubida() != null ? doc.getFechaSubida().format(formatter) : "");
            documentosDTO.add(docDTO);
        }
        res.setDocumentos(documentosDTO);

        return res;
    }

    public void eliminar(String id) {
        if (!tramiteRepository.existsById(id)) {
            throw new NotFoundException("Tramite no encontrado");
        }
        tramiteRepository.deleteById(id);
        completadaRepository.deleteByTramiteId(id);
        documentoRepository.deleteByTramiteId(id);
    }
}

