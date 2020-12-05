package ru.testtask.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;
import ru.testtask.model.MessageType;
import ru.testtask.repo.MessageRepo;

import java.util.Date;
import java.util.Optional;


import static java.lang.String.format;

@Service
public class ChatMessageService {

    private static final String CHAT_ROOM_TOPIC = "/channel";

    @Value("${chat.message.max-length}")
    private long maxMessageLength;

    private final SimpMessageSendingOperations messagingTemplate;

    private final MessageRepo messageRepo;

    private final ChatRoomService chatRoomService;

    public ChatMessageService(MessageRepo messageRepo, SimpMessageSendingOperations messagingTemplate,
                              ChatRoomService chatRoomService) {
        this.messageRepo = messageRepo;
        this.messagingTemplate = messagingTemplate;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage addMessage(ChatMessage chatMessage, String roomId){
        Optional<ChatRoom> optionalChatRoom = chatRoomService.getRoomById(roomId);

        if (chatMessage.getContent().length() < maxMessageLength && optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            chatMessage.setChatRoom(chatRoom);
            chatMessage.setDate(new Date());
            return messageRepo.insert(chatMessage);
        }
        else {
            chatMessage.setType(MessageType.ERROR);
            return chatMessage;
        }
    }

    public Page<ChatMessage> findAllByRoom(Pageable pageable, String roomId){
        return messageRepo.findByChatRoomId(pageable, roomId);
    }

    public void sendMessageToChat(String roomId, ChatMessage chatMessage){
        messagingTemplate.convertAndSend(format(CHAT_ROOM_TOPIC + "/%s", roomId), chatMessage);
    }

    public ChatMessage createLeaveMessage(ChatMessage chatMessage){
        ChatMessage leaveChatMessage = new ChatMessage();
        leaveChatMessage.setType(MessageType.LEAVE);
        leaveChatMessage.setSender(chatMessage.getSender());
        return leaveChatMessage;
    }
}
