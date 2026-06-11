package bo.edu.uagrm.backend.controller;

import bo.edu.uagrm.backend.dto.DocumentoUploadResponse;
import bo.edu.uagrm.backend.exception.NotFoundException;
import bo.edu.uagrm.backend.services.DocumentoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoUploadResponse upload(
            @RequestParam String usuarioId,
            @RequestParam(required = false) String tramiteId,
            @RequestPart("file") MultipartFile file
    ) {
        return documentoService.subirDocumento(usuarioId, tramiteId, file);
    }

    @GetMapping("/archivo/{nombreArchivo:.+}")
    public ResponseEntity<Resource> descargar(@PathVariable String nombreArchivo) {
        Path archivo = documentoService.obtenerRutaArchivo(nombreArchivo);
        try {
            UrlResource recurso = new UrlResource(archivo.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                    .body(recurso);
        } catch (Exception e) {
            throw new NotFoundException("Archivo no encontrado");
        }
    }
}
