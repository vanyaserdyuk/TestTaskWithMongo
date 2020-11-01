package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.User;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
        User findByUsername(String username);
        Optional<User> findById(String id);
    }
