package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import ru.testtask.dto.ChatMessageDTO;
import ru.testtask.model.ChatMessage;
import ru.testtask.service.ChatMessageService;


@Controller
public class ChatWsController {

    private final ModelMapper modelMapper;

    private final ChatMessageService chatMessageService;

    private static final String WS_SESSION_ATTRIBUTE_USERNAME = "username";
    private static final String WS_SESSION_ATTRIBUTE_ROOM_ID = "room_id";

    public ChatWsController(ChatMessageService chatMessageService, ModelMapper modelMapper) {
        this.chatMessageService = chatMessageService;
        this.modelMapper = modelMapper;
    }

    @MessageMapping("/ws/chat/room/{roomId}/message/send")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage messageToSend = chatMessageService.addMessage(modelMapper.map(chatMessageDTO, ChatMessage.class), roomId);
        chatMessageService.sendMessageToChat(roomId, messageToSend);
    }

    @MessageMapping("/ws/chat/room/{roomId}/user/add")
    public void addUserToRoom(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put(WS_SESSION_ATTRIBUTE_ROOM_ID, roomId);
        if (currentRoomId != null) {
            chatMessageService.sendMessageToChat(currentRoomId, chatMessageService.createLeaveMessage(chatMessage));
        }
        headerAccessor.getSessionAttributes().put(WS_SESSION_ATTRIBUTE_USERNAME, chatMessage.getSender());
        chatMessageService.sendMessageToChat(roomId, chatMessage);
    }
}
