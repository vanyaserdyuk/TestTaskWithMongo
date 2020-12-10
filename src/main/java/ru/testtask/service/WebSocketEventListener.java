package ru.testtask.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static ru.testtask.service.ChatMessageServiceImpl.WS_SESSION_ATTRIBUTE_ROOM_ID;
import static ru.testtask.service.ChatMessageServiceImpl.WS_SESSION_ATTRIBUTE_USERNAME;

@Slf4j
@Component
public class WebSocketEventListener {

    private final ChatMessageServiceImpl chatMessageService;

    public WebSocketEventListener(ChatMessageServiceImpl chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.debug("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get(WS_SESSION_ATTRIBUTE_USERNAME);
        String roomId = (String) headerAccessor.getSessionAttributes().get(WS_SESSION_ATTRIBUTE_ROOM_ID);
        if (username != null) {
            log.debug("User Disconnected: " + username);
            chatMessageService.sendUserLeaveFromRoom(roomId, username);
        }
    }
}
