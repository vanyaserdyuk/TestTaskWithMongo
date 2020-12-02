package ru.testtask.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;
import ru.testtask.service.ChatRoomService;
import ru.testtask.service.ChatMessageService;


@Controller
public class ChatWsController {
    private final ChatMessageService chatMessageService;

    private static final String WS_SESSION_ATTRIBUTE_USERNAME = "username";
    private static final String WS_SESSION_ATTRIBUTE_ROOM_ID = "room_id";

    public ChatWsController(SimpMessageSendingOperations messagingTemplate, ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat/{roomName}/sendMessage")
    public void sendMessage(@DestinationVariable String roomName, @Payload ChatMessage chatMessage) {
        ChatMessage messageToSend = chatMessageService.addMessage(chatMessage, roomName);
        chatMessageService.sendMessageToChat(roomName, messageToSend);
    }

    @MessageMapping("/chat/{roomName}/addUser")
    public void addUserToRoom(@DestinationVariable String roomName, @Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put(WS_SESSION_ATTRIBUTE_ROOM_ID, roomName);
        if (currentRoomId != null) {
            chatMessageService.sendMessageToChat(currentRoomId, chatMessageService.createLeaveMessage(chatMessage));
        }
        headerAccessor.getSessionAttributes().put(WS_SESSION_ATTRIBUTE_USERNAME, chatMessage.getSender());
        chatMessageService.sendMessageToChat(roomName, chatMessage);
    }
}
