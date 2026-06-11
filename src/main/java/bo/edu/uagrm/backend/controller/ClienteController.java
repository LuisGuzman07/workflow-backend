package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.ClienteRequest;
import bo.edu.uagrm.backend.dto.ClienteResponse;
import bo.edu.uagrm.backend.model.Cliente;
import bo.edu.uagrm.backend.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.fromEntity(cliente));
    }

    @GetMapping
    public List<ClienteResponse> listarClientes() {
        return clienteService.listar().stream().map(ClienteResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerClientePorId(@PathVariable String id) {
        return ClienteResponse.fromEntity(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizarCliente(@PathVariable String id, @Valid @RequestBody ClienteRequest request) {
        return ClienteResponse.fromEntity(clienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
