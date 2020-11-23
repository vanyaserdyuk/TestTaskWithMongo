package ru.testtask.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.testtask.model.Message;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
public class WebSocketEventListener {

    private final List<String> usernames = new ArrayList<String>();
    private final String dest = "/channel";

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @PostConstruct
    public void init(){
        Thread heartBeat = new Thread(new Runnable() {
            @Override
            public void run(){
                usernames.forEach(username -> messagingTemplate.convertAndSendToUser(username, dest, "ok"));
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    log.error("Thread was interrupted");
                }
            }
        });

        heartBeat.setDaemon(true);
        heartBeat.start();
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
        if (username != null) {
            log.info("User Disconnected: " + username);

            Message message = new Message();
            message.setType(Message.MessageType.LEAVE);
            message.setSender(username);

            messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
        }
    }

    public void addUsername(String username){
        this.usernames.add(username);
    }

}
