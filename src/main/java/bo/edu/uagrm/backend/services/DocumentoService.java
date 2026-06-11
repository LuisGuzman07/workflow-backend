package bo.edu.uagrm.backend.services;

import bo.edu.uagrm.backend.dto.DocumentoUploadResponse;
import bo.edu.uagrm.backend.exception.ConflictException;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.model.DocumentoAdjunto;
import bo.edu.uagrm.backend.repository.DocumentoAdjuntoRepository;
import bo.edu.uagrm.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentoService {

    private static final Path DOCUMENTOS_DIR = Path.of("uploads", "documentos");

    private final DocumentoAdjuntoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;

    public DocumentoService(
            DocumentoAdjuntoRepository documentoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DocumentoUploadResponse subirDocumento(String usuarioId, String tramiteId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ConflictException("Debe seleccionar un archivo");
        }
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        try {
            Files.createDirectories(DOCUMENTOS_DIR);
            String nombreOriginal = file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename();
            String nombreArchivo = construirNombreArchivo(nombreOriginal);
            Path destino = DOCUMENTOS_DIR.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), destino);

            DocumentoAdjunto doc = new DocumentoAdjunto();
            doc.setNombre(nombreOriginal);
            doc.setTipo(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
            doc.setTramiteId(tramiteId);
            doc.setUsuarioId(usuarioId);
            doc.setUrl("/api/documentos/archivo/" + nombreArchivo);
            doc.setFechaSubida(LocalDateTime.now());

            DocumentoAdjunto saved = documentoRepository.save(doc);
            DocumentoUploadResponse response = new DocumentoUploadResponse();
            response.setId(saved.getId());
            response.setNombre(saved.getNombre());
            response.setTipo(saved.getTipo());
            response.setUrl(saved.getUrl());
            response.setTramiteId(saved.getTramiteId());
            response.setFechaSubida(saved.getFechaSubida());
            return response;
        } catch (IOException e) {
            throw new ConflictException("No se pudo subir el archivo");
        }
    }

    public Path obtenerRutaArchivo(String nombreArchivo) {
        Path directorioBase = DOCUMENTOS_DIR.toAbsolutePath().normalize();
        Path archivo = directorioBase.resolve(nombreArchivo).normalize();
        if (!archivo.startsWith(directorioBase) || !Files.exists(archivo)) {
            throw new NotFoundException("Archivo no encontrado");
        }
        return archivo;
    }

    private String construirNombreArchivo(String nombreOriginal) {
        String nombreLimpio = nombreOriginal
                .replaceAll("[\\\\/]+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        return UUID.randomUUID() + "_" + nombreLimpio;
    }
}
