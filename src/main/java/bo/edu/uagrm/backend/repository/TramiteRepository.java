package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.Tramite;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TramiteRepository extends MongoRepository<Tramite, String> {
    List<Tramite> findByClienteId(String clienteId);
    List<Tramite> findByEstado(String estado);
    Optional<Tramite> findByCodigo(String codigo);
}
