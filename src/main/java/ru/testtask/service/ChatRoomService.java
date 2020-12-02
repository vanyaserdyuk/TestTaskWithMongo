package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;
import ru.testtask.model.ChatRoom;
import ru.testtask.repo.ChatRoomRepo;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepo chatRoomRepo;

    private final MongoTemplate mongoTemplate;

    public ChatRoomService(ChatRoomRepo chatRoomRepo, MongoTemplate mongoTemplate) {
        this.chatRoomRepo = chatRoomRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void init(){
        mongoTemplate.indexOps("chatRooms").ensureIndex(new Index("roomName", Sort.Direction.ASC).unique());
    }

    public ChatRoom addChatRoomIfNotExists(String roomName){
        if (chatRoomRepo.findByRoomName(roomName) == null) {
            return chatRoomRepo.insert(ChatRoom.builder().roomName(roomName).build());
        }
        else return chatRoomRepo.findByRoomName(roomName);
    }

    public ChatRoom findByRoomName(String roomName){
        return chatRoomRepo.findByRoomName(roomName);
    }

    public List<ChatRoom> getAllRooms(){
        return chatRoomRepo.findAll();
    }

}
