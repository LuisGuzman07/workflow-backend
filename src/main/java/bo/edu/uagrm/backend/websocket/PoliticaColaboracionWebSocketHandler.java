package bo.edu.uagrm.backend.websocket;

import bo.edu.uagrm.backend.dto.UsuarioResponse;
import bo.edu.uagrm.backend.services.PoliticaNegocioService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PoliticaColaboracionWebSocketHandler extends TextWebSocketHandler {

    private final PoliticaNegocioService politicaNegocioService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Set<WebSocketSession>> sessionsByPolicy = new ConcurrentHashMap<>();
    private final Map<String, SessionInfo> sessionInfoById = new ConcurrentHashMap<>();

    public PoliticaColaboracionWebSocketHandler(PoliticaNegocioService politicaNegocioService) {
        this.politicaNegocioService = politicaNegocioService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SessionInfo sessionInfo = parseSessionInfo(session);
        if (sessionInfo == null || !politicaNegocioService.puedeEditar(sessionInfo.policyId(), sessionInfo.userId())) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("No autorizado para colaborar en esta politica"));
            return;
        }

        sessionsByPolicy.computeIfAbsent(sessionInfo.policyId(), key -> ConcurrentHashMap.newKeySet()).add(session);
        sessionInfoById.put(session.getId(), sessionInfo);

        sendJson(session, Map.of(
                "type", "connected",
                "policyId", sessionInfo.policyId(),
                "userId", sessionInfo.userId()
        ));
        broadcastPresence(sessionInfo.policyId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SessionInfo info = sessionInfoById.remove(session.getId());
        if (info == null) {
            return;
        }

        Set<WebSocketSession> sessions = sessionsByPolicy.get(info.policyId());
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionsByPolicy.remove(info.policyId());
            }
        }
        broadcastPresence(info.policyId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SessionInfo info = sessionInfoById.get(session.getId());
        if (info == null || !session.isOpen()) {
            return;
        }

        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = payload.get("type") == null ? "" : payload.get("type").toString().trim();
        if (!"diagram-changed".equals(type)) {
            return;
        }

        if (!politicaNegocioService.puedeEditar(info.policyId(), info.userId())) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("No autorizado para editar esta politica"));
            return;
        }

        Object diagram = payload.get("diagram");
        if (!(diagram instanceof String rawDiagram) || !StringUtils.hasText(rawDiagram)) {
            return;
        }

        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("type", "diagram-changed");
        outbound.put("policyId", info.policyId());
        outbound.put("actorUserId", info.userId());
        outbound.put("actor", politicaNegocioService.obtenerUsuarioResumen(info.userId()));
        outbound.put("diagram", rawDiagram);
        broadcastPolicyEvent(info.policyId(), outbound);
    }

    public void broadcastPolicyEvent(String policyId, Map<String, Object> payload) {
        Set<WebSocketSession> sessions = sessionsByPolicy.get(policyId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        List<WebSocketSession> snapshot = new ArrayList<>(sessions);
        for (WebSocketSession session : snapshot) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                sendJson(session, payload);
            } catch (IOException ex) {
                tryClose(session);
                sessions.remove(session);
            }
        }
    }

    public void refreshAuthorizedSessions(String policyId) {
        Set<WebSocketSession> sessions = sessionsByPolicy.get(policyId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        List<WebSocketSession> snapshot = new ArrayList<>(sessions);
        for (WebSocketSession session : snapshot) {
            SessionInfo info = sessionInfoById.get(session.getId());
            if (info == null) {
                continue;
            }
            if (!politicaNegocioService.puedeEditar(policyId, info.userId())) {
                tryClose(session);
                sessions.remove(session);
                sessionInfoById.remove(session.getId());
            }
        }
        broadcastPresence(policyId);
    }

    private void broadcastPresence(String policyId) {
        Set<WebSocketSession> sessions = sessionsByPolicy.get(policyId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        List<UsuarioResponse> onlineUsers = sessions.stream()
                .map(session -> sessionInfoById.get(session.getId()))
                .filter(java.util.Objects::nonNull)
                .map(SessionInfo::userId)
                .distinct()
                .map(politicaNegocioService::obtenerUsuarioResumen)
                .filter(java.util.Objects::nonNull)
                .toList();

        broadcastPolicyEvent(policyId, Map.of(
                "type", "presence",
                "policyId", policyId,
                "onlineUsers", onlineUsers
        ));
    }

    private SessionInfo parseSessionInfo(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }

        String path = uri.getPath();
        if (!StringUtils.hasText(path)) {
            return null;
        }

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == path.length() - 1) {
            return null;
        }

        String policyId = path.substring(lastSlash + 1);
        String userId = extractQueryParam(uri.getQuery(), "userId");
        if (!StringUtils.hasText(policyId) || !StringUtils.hasText(userId)) {
            return null;
        }

        return new SessionInfo(policyId.trim(), userId.trim());
    }

    private String extractQueryParam(String query, String name) {
        if (!StringUtils.hasText(query)) {
            return null;
        }

        for (String part : query.split("&")) {
            String[] pieces = part.split("=", 2);
            if (pieces.length == 2 && name.equals(pieces[0]) && StringUtils.hasText(pieces[1])) {
                return pieces[1];
            }
        }
        return null;
    }

    private void sendJson(WebSocketSession session, Map<String, Object> payload) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }

    private void tryClose(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException ignored) {
        }
    }

    private record SessionInfo(String policyId, String userId) {
    }
}
