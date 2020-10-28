package ru.testtask.repo;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.Project;

import java.util.Optional;

@Repository
public interface ProjectRepo extends MongoRepository<Project, String> {
    Optional<Project> findById(String id);
    Project findByName(String name);
}
