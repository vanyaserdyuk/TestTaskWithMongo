package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.testtask.model.ChatMessage;

public interface MessageRepo extends MongoRepository<ChatMessage, String> {
}
