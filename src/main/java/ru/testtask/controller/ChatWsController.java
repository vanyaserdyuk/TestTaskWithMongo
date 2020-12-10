package ru.testtask.controller;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import ru.testtask.dto.ChatMessageUserDTO;
import ru.testtask.service.ChatMessageServiceImpl;

import static ru.testtask.service.ChatMessageServiceImpl.WS_SESSION_ATTRIBUTE_USERNAME;


@Controller
public class ChatWsController {

    private final ChatMessageServiceImpl chatMessageServiceImpl;

    public ChatWsController(ChatMessageServiceImpl chatMessageServiceImpl) {
        this.chatMessageServiceImpl = chatMessageServiceImpl;
    }

    @MessageMapping("/ws/chat/room/{roomId}/message/send")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageUserDTO chatMessageDTO) {
        chatMessageServiceImpl.sendUserMessage(roomId, chatMessageDTO.getSender(), chatMessageDTO.getContent());
    }

    @MessageMapping("/ws/chat/room/{roomId}/user/add")
    public void addUserToRoom(@DestinationVariable String roomId, @Payload String username,
                              SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put(WS_SESSION_ATTRIBUTE_USERNAME, username);
        chatMessageServiceImpl.sendUserJoinToRoom(roomId, username);
    }
}
