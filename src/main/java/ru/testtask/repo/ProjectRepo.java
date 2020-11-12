package ru.testtask.repo;


import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.Project;

import java.util.Optional;

@Repository
public interface ProjectRepo extends MongoRepository<Project, String> {
    Project findByName(String name);
}
