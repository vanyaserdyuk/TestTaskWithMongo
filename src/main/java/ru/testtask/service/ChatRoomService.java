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
import java.util.Objects;
import java.util.Optional;

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
        ChatRoom chatRoom = chatRoomRepo.findByRoomName(roomName);
        return Objects.requireNonNullElseGet(chatRoom, () -> chatRoomRepo.insert(ChatRoom.builder().roomName(roomName).build()));
    }

    public List<ChatRoom> getAllRooms(){
        return chatRoomRepo.findAll();
    }

    public Optional<ChatRoom> getRoomById(String id){
        return chatRoomRepo.findById(id);
    }

}
