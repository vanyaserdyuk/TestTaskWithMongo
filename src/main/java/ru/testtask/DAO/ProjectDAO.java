package ru.testtask.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.models.Project;

import java.util.List;

@Repository
public class ProjectDAO{

    @Autowired
    MongoTemplate mongoTemplate;

    public Project findById(int id) {
        Project project = mongoTemplate.findById(id, Project.class);
        return project;
    }

    public void save(Project project) {
        mongoTemplate.insert(project);
    }

    public void update(Project project) {
        mongoTemplate.update(Project.class);
    }

    public void delete(Project project) {
        mongoTemplate.remove(project, "projects");
    }

    public List<Project> findAll() {
        return mongoTemplate.findAll(Project.class);
    }


}
