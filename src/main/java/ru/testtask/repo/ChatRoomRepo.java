package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.ChatRoom;

@Repository
public interface ChatRoomRepo extends MongoRepository<ChatRoom, String> {
    ChatRoom findByRoomName(String roomName);
}
