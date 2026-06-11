package bo.edu.uagrm.backend.config;

import bo.edu.uagrm.backend.websocket.PoliticaColaboracionWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PoliticaColaboracionWebSocketHandler politicaColaboracionWebSocketHandler;

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:4200,https://project-3a2efd93-c520-428d-977.web.app}")
    private String corsAllowedOrigins;

    public WebSocketConfig(PoliticaColaboracionWebSocketHandler politicaColaboracionWebSocketHandler) {
        this.politicaColaboracionWebSocketHandler = politicaColaboracionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(politicaColaboracionWebSocketHandler, "/ws/politicas/*")
                .setAllowedOrigins(corsAllowedOrigins.split(","));
    }
}
