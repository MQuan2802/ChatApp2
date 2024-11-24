package ChatApp.MessageExchangeDomain;

import ChatApp.MessageExchangeDomain.Handler.SocketDocumentHandler;
import ChatApp.MessageExchangeDomain.Handler.SocketImageHandler;
import ChatApp.MessageExchangeDomain.Handler.SocketTextHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketTextHandler(), "/text").setAllowedOrigins("*");
        registry.addHandler(new SocketImageHandler(), "/image").setAllowedOrigins("*");
        registry.addHandler(new SocketDocumentHandler(), "/document").setAllowedOrigins("*");
    }
}
