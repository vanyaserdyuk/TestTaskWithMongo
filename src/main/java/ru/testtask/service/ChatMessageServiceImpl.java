package ru.testtask.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import ru.testtask.dto.ChatMessageServiceDTO;
import ru.testtask.dto.ChatMessageUserDTO;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;
import ru.testtask.model.MessageType;
import ru.testtask.repo.MessageRepo;

import java.util.Date;
import java.util.Optional;


import static java.lang.String.format;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    public static final String WS_SESSION_ATTRIBUTE_USERNAME = "username";
    public static final String WS_SESSION_ATTRIBUTE_ROOM_ID = "room_id";
    public static final String CHAT_ROOM_TOPIC = "/channel";

    @Value("${chat.message.max-length:1000}")
    private long maxMessageLength;

    private final SimpMessageSendingOperations messagingTemplate;

    private final MessageRepo messageRepo;

    private final ChatRoomService chatRoomService;

    private final ModelMapper modelMapper;

    public ChatMessageServiceImpl(MessageRepo messageRepo, SimpMessageSendingOperations messagingTemplate,
                                  ChatRoomService chatRoomService, ModelMapper modelMapper) {
        this.messageRepo = messageRepo;
        this.messagingTemplate = messagingTemplate;
        this.chatRoomService = chatRoomService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendUserMessage(String roomId, String username, String content){
        Optional<ChatRoom> optionalChatRoom = chatRoomService.getRoomById(roomId);
        ChatMessage chatMessage = new ChatMessage();

        if (content.length() < maxMessageLength && optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            chatMessage.setChatRoom(chatRoom);
            chatMessage.setContent(content);
            chatMessage.setSender(username);
            chatMessage.setType(MessageType.CHAT);
            chatMessage.setDate(new Date());
            messageRepo.insert(chatMessage);
            messagingTemplate.convertAndSend(CHAT_ROOM_TOPIC + "/" + roomId,
                    modelMapper.map(chatMessage, ChatMessageUserDTO.class));
        }
        else {
            chatMessage.setType(MessageType.ERROR);
            sendServiceMessage(roomId, modelMapper.map(chatMessage, ChatMessageServiceDTO.class));
        }
    }

    @Override
    public Page<ChatMessage> findAllByRoom(String roomId, int page, int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        return messageRepo.findByChatRoomId(pageable, roomId);
    }

    @Override
    public Page<ChatMessage> findAllByRoom(String roomId){
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "date"));
        return messageRepo.findByChatRoomId(pageable, roomId);
    }

    @Override
    public void sendUserJoinToRoom(String roomId, String username){
        ChatMessageServiceDTO chatMessageServiceDTO = ChatMessageServiceDTO.builder()
                .type(MessageType.JOIN).username(username).build();
        sendServiceMessage(roomId, chatMessageServiceDTO);
    }

    @Override
    public void sendUserLeaveFromRoom(String roomId, String username){
        ChatMessageServiceDTO chatMessageServiceDTO = ChatMessageServiceDTO.builder()
                .type(MessageType.LEAVE).username(username).build();
        sendServiceMessage(roomId, chatMessageServiceDTO);
    }

    private void sendServiceMessage(String roomId, ChatMessageServiceDTO chatMessageServiceDTO){
        messagingTemplate.convertAndSend(CHAT_ROOM_TOPIC + "/" + roomId, chatMessageServiceDTO);
    }

}
