package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.testtask.model.Message;
import ru.testtask.repo.MessageRepo;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    public Message addMessage(Message message){
        return messageRepo.insert(message);
    }

    public List<Message> getAllMessages(){
        return messageRepo.findAll();
    }
}
