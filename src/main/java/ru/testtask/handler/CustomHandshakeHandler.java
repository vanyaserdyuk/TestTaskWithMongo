package ru.testtask.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.testtask.controller.WebSocketEventListener;
import ru.testtask.model.StompPrincipal;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String name = UUID.randomUUID().toString();
        webSocketEventListener.addUsername(name);
        return new StompPrincipal(name);
    }
}
