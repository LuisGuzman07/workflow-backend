package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.PoliticaColaboradoresResponse;
import bo.edu.uagrm.backend.dto.PoliticaColaboradoresUpdateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioCreateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioEditRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioResponse;
import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.services.PoliticaNegocioService;
import bo.edu.uagrm.backend.websocket.PoliticaColaboracionNotifier;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/politicas-negocio", "/api/politicas"})
public class PoliticaNegocioController {

    private final PoliticaNegocioService politicaNegocioService;
    private final PoliticaColaboracionNotifier politicaColaboracionNotifier;

    public PoliticaNegocioController(
            PoliticaNegocioService politicaNegocioService,
            PoliticaColaboracionNotifier politicaColaboracionNotifier
    ) {
        this.politicaNegocioService = politicaNegocioService;
        this.politicaColaboracionNotifier = politicaColaboracionNotifier;
    }

    @PostMapping
    public ResponseEntity<PoliticaNegocioResponse> crearPolitica(@Valid @RequestBody PoliticaNegocioCreateRequest request) {
        PoliticaNegocio politica = politicaNegocioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PoliticaNegocioResponse.fromEntity(politica));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoliticaNegocioResponse> editarPolitica(
            @PathVariable String id,
            @Valid @RequestBody PoliticaNegocioEditRequest request
    ) {
        PoliticaNegocio politica = politicaNegocioService.editar(id, request);
        politicaColaboracionNotifier.notificarPoliticaActualizada(politica, request.getUsuarioSolicitanteId());
        return ResponseEntity.ok(PoliticaNegocioResponse.fromEntity(politica));
    }

    @GetMapping
    public List<PoliticaNegocioResponse> listarPoliticas() {
        return politicaNegocioService.listar()
                .stream()
                .map(PoliticaNegocioResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public PoliticaNegocioResponse obtenerPoliticaPorId(
            @PathVariable String id,
            @RequestParam(required = false) String usuarioSolicitanteId
    ) {
        PoliticaNegocio politica = politicaNegocioService.obtenerPorId(id, usuarioSolicitanteId);
        return PoliticaNegocioResponse.fromEntity(politica);
    }

    @GetMapping("/{id}/colaboradores")
    public PoliticaColaboradoresResponse obtenerColaboradores(
            @PathVariable String id,
            @RequestParam String usuarioSolicitanteId
    ) {
        return politicaNegocioService.obtenerColaboradores(id, usuarioSolicitanteId);
    }

    @GetMapping("/{id}/colaboradores/disponibles")
    public List<UsuarioResponse> buscarAdministradoresDisponibles(
            @PathVariable String id,
            @RequestParam String usuarioSolicitanteId,
            @RequestParam(required = false) String q
    ) {
        return politicaNegocioService.buscarAdministradoresDisponibles(id, usuarioSolicitanteId, q);
    }

    @PutMapping("/{id}/colaboradores")
    public PoliticaColaboradoresResponse actualizarColaboradores(
            @PathVariable String id,
            @Valid @RequestBody PoliticaColaboradoresUpdateRequest request
    ) {
        PoliticaColaboradoresResponse response = politicaNegocioService.actualizarColaboradores(id, request);
        PoliticaNegocio politica = politicaNegocioService.obtenerPorId(id);
        politicaColaboracionNotifier.notificarColaboradoresActualizados(politica, response, request.getUsuarioSolicitanteId());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPolitica(
            @PathVariable String id,
            @RequestParam String usuarioSolicitanteId
    ) {
        politicaNegocioService.eliminar(id, usuarioSolicitanteId);
        politicaColaboracionNotifier.notificarPoliticaEliminada(id, usuarioSolicitanteId);
        return ResponseEntity.noContent().build();
    }
}
