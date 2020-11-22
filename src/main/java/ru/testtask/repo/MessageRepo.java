package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.testtask.model.Message;

public interface MessageRepo extends MongoRepository<Message, String> {
}
