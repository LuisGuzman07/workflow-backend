package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RolRepository extends MongoRepository<Rol, String> {
	boolean existsByNombreIgnoreCase(String nombre);
	Optional<Rol> findByNombreIgnoreCase(String nombre);
}