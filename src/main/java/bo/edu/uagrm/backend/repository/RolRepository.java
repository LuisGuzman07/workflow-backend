package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RolRepository extends MongoRepository<Rol, String> {
	boolean existsByNombreIgnoreCase(String nombre);
}