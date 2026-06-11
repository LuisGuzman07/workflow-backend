package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.IniciarTramiteRequest;
import bo.edu.uagrm.backend.dto.TramiteEstadoResponse;
import bo.edu.uagrm.backend.dto.TramiteResumenResponse;
import bo.edu.uagrm.backend.dto.TramiteDetalleResponse;
import bo.edu.uagrm.backend.model.Tramite;
import bo.edu.uagrm.backend.services.TramiteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tramites")
public class TramiteController {

    private final TramiteService tramiteService;

    public TramiteController(TramiteService tramiteService) {
        this.tramiteService = tramiteService;
    }

    @PostMapping("/iniciar")
    public Tramite iniciar(@Valid @RequestBody IniciarTramiteRequest request) {
        return tramiteService.iniciar(request);
    }

    @GetMapping("/cliente")
    public List<TramiteEstadoResponse> listarSeguimientoCliente(@RequestParam String usuarioId) {
        return tramiteService.listarSeguimientoCliente(usuarioId);
    }

    @GetMapping
    public List<TramiteResumenResponse> listarTodos() {
        return tramiteService.listarTodos();
    }

    @GetMapping("/{id}/detalle")
    public TramiteDetalleResponse obtenerDetalle(@PathVariable String id) {
        return tramiteService.obtenerDetalle(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable String id) {
        tramiteService.eliminar(id);
        return Map.of("mensaje", "Tramite eliminado exitosamente");
    }
}

