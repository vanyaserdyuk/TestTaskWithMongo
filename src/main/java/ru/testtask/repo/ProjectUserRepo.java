package ru.testtask.repo;

import org.springframework.stereotype.Repository;
import ru.testtask.model.ProjectUser;

@Repository
public interface ProjectUserRepo {
        ProjectUser findByUsername(String username);
    }

