package ru.testtask.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.testtask.model.ChatMessage;
import ru.testtask.model.ChatRoom;

public interface MessageRepo extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findByChatRoom(Pageable pageable, ChatRoom chatRoom);
}
