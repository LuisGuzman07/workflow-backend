package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.Area;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AreaRepository extends MongoRepository<Area, String> {
	boolean existsByNombreIgnoreCase(String nombre);
}