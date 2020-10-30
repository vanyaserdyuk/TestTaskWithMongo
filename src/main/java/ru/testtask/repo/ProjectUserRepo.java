package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;
import ru.testtask.model.ProjectUser;

@Repository
public interface ProjectUserRepo extends MongoRepository<ProjectUser, String> {
    ProjectUser findByUsername(String username);
}
