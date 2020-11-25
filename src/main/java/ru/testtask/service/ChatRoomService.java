package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.testtask.model.ChatRoom;
import ru.testtask.repo.ChatRoomRepo;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepo chatRoomRepo;

    public void addChatRoom(ChatRoom chatRoom){
        if (chatRoomRepo.findByRoomId(chatRoom.getRoomId()) == null)
        chatRoomRepo.insert(chatRoom);
    }

}
