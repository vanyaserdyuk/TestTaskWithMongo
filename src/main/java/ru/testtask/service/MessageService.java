package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.testtask.model.ChatMessage;
import ru.testtask.repo.MessageRepo;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    public ChatMessage addMessage(ChatMessage chatMessage){
        return messageRepo.insert(chatMessage);
    }

    public List<ChatMessage> getAllMessages(){
        return messageRepo.findAll();
    }

    public Page<ChatMessage> findForRoom(Pageable pageable, String roomId){
        return messageRepo.findByRoomId(pageable, roomId);
    }
}
