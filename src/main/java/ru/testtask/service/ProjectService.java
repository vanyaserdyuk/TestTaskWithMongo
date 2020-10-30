package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectService {
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    UserService userService;

    public ProjectService() {
    }


    public Optional<Project> findProjectById(String id) {
        return projectRepo.findById(id);
    }

    public Project findProjectByName(String name){
        return projectRepo.findByName(name);
    }


    public Project createProject(Project project) {
        for (int i = 0; i < 10; i++) {
            project.addGeometry(Geometry.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
            project.addAttribute(Attribute.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
        }

        project.setOwnerId(userService.getCurrentUserId());

        projectRepo.insert(project);
        return findProjectByName(project.getName());
    }


    public void deleteProject(String id) {
        projectRepo.deleteById(id);
    }


    public void updateProject(Project project) {
        projectRepo.save(project);
    }


    public List<Project> findAllProjects() {
        return projectRepo.findAll();
    }


}
