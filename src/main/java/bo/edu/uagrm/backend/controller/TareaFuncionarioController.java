package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.CompletarTareaRequest;
import bo.edu.uagrm.backend.dto.SeguimientoTramiteResponse;
import bo.edu.uagrm.backend.dto.TareaPendienteResponse;
import bo.edu.uagrm.backend.services.TareaFuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tareas")
public class TareaFuncionarioController {

    private final TareaFuncionarioService tareaFuncionarioService;

    public TareaFuncionarioController(TareaFuncionarioService tareaFuncionarioService) {
        this.tareaFuncionarioService = tareaFuncionarioService;
    }

    @GetMapping("/pendiente")
    public ResponseEntity<TareaPendienteResponse> obtenerPendiente(@RequestParam String usuarioId) {
        return tareaFuncionarioService.obtenerPendiente(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/pendientes")
    public List<TareaPendienteResponse> obtenerPendientes(@RequestParam String usuarioId) {
        return tareaFuncionarioService.listarPendientes(usuarioId);
    }

    @PostMapping("/completar")
    public Map<String, String> completar(@Valid @RequestBody CompletarTareaRequest request) {
        tareaFuncionarioService.completar(request);
        return Map.of("mensaje", "Tarea completada exitosamente");
    }

    @GetMapping("/seguimiento")
    public List<SeguimientoTramiteResponse> seguimiento(@RequestParam String usuarioId) {
        return tareaFuncionarioService.listarSeguimiento(usuarioId);
    }

    @DeleteMapping("/completadas/{tareaId}")
    public Map<String, String> eliminarCompletada(@PathVariable String tareaId, @RequestParam String usuarioId) {
        tareaFuncionarioService.eliminarCompletada(usuarioId, tareaId);
        return Map.of("mensaje", "Formulario completado eliminado");
    }
}
