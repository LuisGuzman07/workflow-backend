package bo.edu.uagrm.backend.controller;

import jakarta.validation.Valid;
import bo.edu.uagrm.backend.model.Area;
import bo.edu.uagrm.backend.services.AreaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    public ResponseEntity<Area> crearArea(@Valid @RequestBody Area area) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaService.guardar(area));
    }

    @GetMapping
    public List<Area> listarAreas() {
        return areaService.listar();
    }

    @GetMapping("/{id}")
    public Area obtenerAreaPorId(@PathVariable String id) {
        return areaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Area actualizarArea(@PathVariable String id, @Valid @RequestBody Area area) {
        return areaService.actualizar(id, area);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArea(@PathVariable String id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}