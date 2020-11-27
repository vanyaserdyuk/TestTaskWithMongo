package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;
import ru.testtask.service.ChatRoomService;
import ru.testtask.service.MessageService;

import static java.lang.String.format;

@Controller
public class ChatWsController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Value("${maxMessageLength}")
    private long maxMessageLength;

    private static final String ROOM_ID_MESSAGE_ATTRIBUTE = "room_id";

    @MessageMapping("/chat/{roomName}/sendMessage")
    public void sendMessage(@DestinationVariable String roomName, @Payload ChatMessage chatMessage) {
        if (chatMessage.getContent().length() < maxMessageLength) {
            ChatRoom chatRoom = chatRoomService.addChatRoom(roomName);
            chatMessage.setChatRoom(chatRoom);
            messageService.addMessage(chatMessage);
        }
        else {
            chatMessage.setType(ChatMessage.MessageType.ERROR);
        }
        messagingTemplate.convertAndSend(format("/channel/%s", roomName), chatMessage);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUserToRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put(ROOM_ID_MESSAGE_ATTRIBUTE, roomId);
        if (currentRoomId != null) {
            ChatMessage leaveChatMessage = new ChatMessage();
            leaveChatMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveChatMessage.setSender(chatMessage.getSender());
            messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveChatMessage);
        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
    }


}
