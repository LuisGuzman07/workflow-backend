package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.PoliticaNegocio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PoliticaNegocioRepository extends MongoRepository<PoliticaNegocio, String> {
}
