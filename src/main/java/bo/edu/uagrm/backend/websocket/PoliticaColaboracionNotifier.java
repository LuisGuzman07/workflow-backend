package bo.edu.uagrm.backend.websocket;

import bo.edu.uagrm.backend.dto.PoliticaColaboradoresResponse;
import bo.edu.uagrm.backend.dto.PoliticaNegocioResponse;
import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.model.PoliticaNegocio;
import bo.edu.uagrm.backend.services.PoliticaNegocioService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PoliticaColaboracionNotifier {

    private final PoliticaColaboracionWebSocketHandler webSocketHandler;
    private final PoliticaNegocioService politicaNegocioService;

    public PoliticaColaboracionNotifier(
            PoliticaColaboracionWebSocketHandler webSocketHandler,
            PoliticaNegocioService politicaNegocioService
    ) {
        this.webSocketHandler = webSocketHandler;
        this.politicaNegocioService = politicaNegocioService;
    }

    public void notificarPoliticaActualizada(PoliticaNegocio politica, String actorUserId) {
        UsuarioResponse actor = politicaNegocioService.obtenerUsuarioResumen(actorUserId);
        Map<String, Object> payload = basePayload("policy-updated", politica.getId(), actorUserId, actor);
        payload.put("politica", PoliticaNegocioResponse.fromEntity(politica));
        webSocketHandler.broadcastPolicyEvent(politica.getId(), payload);
    }

    public void notificarColaboradoresActualizados(PoliticaNegocio politica, PoliticaColaboradoresResponse colaboradores, String actorUserId) {
        UsuarioResponse actor = politicaNegocioService.obtenerUsuarioResumen(actorUserId);
        Map<String, Object> payload = basePayload("collaborators-updated", politica.getId(), actorUserId, actor);
        payload.put("politica", PoliticaNegocioResponse.fromEntity(politica));
        payload.put("collaborators", colaboradores);
        webSocketHandler.broadcastPolicyEvent(politica.getId(), payload);
        webSocketHandler.refreshAuthorizedSessions(politica.getId());
    }

    public void notificarPoliticaEliminada(String politicaId, String actorUserId) {
        UsuarioResponse actor = politicaNegocioService.obtenerUsuarioResumen(actorUserId);
        webSocketHandler.broadcastPolicyEvent(politicaId, basePayload("policy-deleted", politicaId, actorUserId, actor));
        webSocketHandler.refreshAuthorizedSessions(politicaId);
    }

    private Map<String, Object> basePayload(String type, String politicaId, String actorUserId, UsuarioResponse actor) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", type);
        payload.put("policyId", politicaId);
        payload.put("actorUserId", actorUserId);
        payload.put("timestamp", LocalDateTime.now().toString());
        if (actor != null) {
            payload.put("actor", actor);
        }
        return payload;
    }
}
