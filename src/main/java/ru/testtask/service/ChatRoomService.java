package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;
import ru.testtask.repo.ChatRoomRepo;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepo chatRoomRepo;

    public void addChatRoom(ChatRoom chatRoom){
        if (chatRoomRepo.findByRoomName(chatRoom.getRoomName()) == null)
        chatRoomRepo.insert(chatRoom);
    }

    public void addMessageToChatRoom(ChatMessage chatMessage){
        ChatRoom chatRoom = chatRoomRepo.findByRoomName(chatMessage.getRoomName());
        chatRoom.addMessage(chatMessage);
        chatRoomRepo.save(chatRoom);
    }

}
