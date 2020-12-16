package ru.testtask.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import ru.testtask.model.Message;
import ru.testtask.service.MessageService;
import ru.testtask.service.ServerHeartbeatService;

import java.security.Principal;
import java.util.List;

import static java.lang.String.format;

@Controller
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;

    private final MessageService messageService;

    private final ServerHeartbeatService serverHeartbeatService;

    private final WebSocketEventListener webSocketEventListener;

    public ChatController(SimpMessageSendingOperations messagingTemplate, MessageService messageService, ServerHeartbeatService serverHeartbeatService, WebSocketEventListener webSocketEventListener) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.serverHeartbeatService = serverHeartbeatService;
        this.webSocketEventListener = webSocketEventListener;
    }

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload Message message) {
        if (message.getContent().length() > 1000){
            message.setContent(message.getContent().substring(0, 1000));
        }

        messageService.addMessage(message);

        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload Message message,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
        if (currentRoomId != null) {
            Message leaveMessage = new Message();
            leaveMessage.setType(Message.MessageType.LEAVE);
            leaveMessage.setSender(message.getSender());
            messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveMessage);
        }
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), message);

        List<Message> messages = messageService.getAllMessages();
        messages.forEach(message1 -> messagingTemplate.convertAndSend(format("/channel/%s", roomId), message));
    }

    @MessageMapping("/hello")
    @SendToUser("/topic/greetings")
    public Message greeting(Message message, Principal principal) {
        log.info("Received greeting message {} from {}", message, principal.getName());
        webSocketEventListener.addUsername(principal.getName());
        return new Message("Hello!");
    }
}
