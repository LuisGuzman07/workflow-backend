package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.DocumentoAdjunto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentoAdjuntoRepository extends MongoRepository<DocumentoAdjunto, String> {
    java.util.List<DocumentoAdjunto> findByTramiteId(String tramiteId);
    void deleteByTramiteId(String tramiteId);

}

