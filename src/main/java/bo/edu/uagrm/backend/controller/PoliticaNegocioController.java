package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.PoliticaNegocioCreateRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioEditRequest;
import bo.edu.uagrm.backend.dto.PoliticaNegocioResponse;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.services.PoliticaNegocioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/politicas-negocio", "/api/politicas"})
public class PoliticaNegocioController {

    private final PoliticaNegocioService politicaNegocioService;

    public PoliticaNegocioController(PoliticaNegocioService politicaNegocioService) {
        this.politicaNegocioService = politicaNegocioService;
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
    public PoliticaNegocioResponse obtenerPoliticaPorId(@PathVariable String id) {
        PoliticaNegocio politica = politicaNegocioService.obtenerPorId(id);
        return PoliticaNegocioResponse.fromEntity(politica);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPolitica(@PathVariable String id) {
        politicaNegocioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
