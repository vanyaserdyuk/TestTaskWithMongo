package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

import static java.lang.String.format;

@Controller
public class ChatWsController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        if (chatMessage.getContent().length() > 1000){
            chatMessage.setContent(chatMessage.getContent().substring(0, 1000));
        }

        chatMessage.setRoomId(roomId);
        messageService.addMessage(chatMessage);
        chatRoomService.addChatRoom(ChatRoom.builder().roomId(roomId).build());

        messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
        if (currentRoomId != null) {
            ChatMessage leaveChatMessage = new ChatMessage();
            leaveChatMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveChatMessage.setSender(chatMessage.getSender());
            messagingTemplate.convertAndSend(format("/channel/%s", currentRoomId), leaveChatMessage);
        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);

        List<ChatMessage> chatMessages = messageService.getAllMessages();
//        messages.forEach(message1 -> messagingTemplate.convertAndSend(format("/channel/%s", roomId), message));
    }


}
