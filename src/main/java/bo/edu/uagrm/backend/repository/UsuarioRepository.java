package bo.edu.uagrm.backend.repository;

import bo.edu.uagrm.backend.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
	Optional<Usuario> findByCorreoIgnoreCase(String correo);
	boolean existsByCorreoIgnoreCase(String correo);
	boolean existsByCorreoIgnoreCaseAndIdNot(String correo, String id);
}