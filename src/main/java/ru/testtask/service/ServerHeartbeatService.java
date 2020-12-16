package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.testtask.controller.WebSocketEventListener;



@Service
public class ServerHeartbeatService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Scheduled(fixedRate = 60000)
    public void sendHeartBeat(){
        WebSocketEventListener.usernames.forEach(username ->
                messagingTemplate.convertAndSendToUser(username, WebSocketEventListener.dest, "ok"));
    }

}
