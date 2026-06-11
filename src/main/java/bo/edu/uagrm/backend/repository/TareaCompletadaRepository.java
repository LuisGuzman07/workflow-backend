package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.TareaCompletada;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TareaCompletadaRepository extends MongoRepository<TareaCompletada, String> {
    boolean existsByUsuarioIdAndPoliticaIdAndAreaId(String usuarioId, String politicaId, String areaId);
    boolean existsByUsuarioIdAndPoliticaIdAndAreaIdAndNodeId(String usuarioId, String politicaId, String areaId, String nodeId);
    void deleteByTramiteId(String tramiteId);

}
