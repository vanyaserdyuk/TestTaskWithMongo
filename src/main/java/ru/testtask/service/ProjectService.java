package ru.testtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.testtask.exception.WrongMethodUseException;
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
    private UserService userService;

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
        try {
            project.setOwnerId(userService.getCurrentUserId());
        }
        catch(WrongMethodUseException e){
            e.printStackTrace();
        }
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

    public boolean isOwner(String id){
        Project project;

        try {
            Optional<Project> optionalProject = findProjectById(id);

            if (optionalProject.isPresent()){
                project = optionalProject.get();
            }
            else return false;

           return userService.getCurrentUserId().equals(project.getOwnerId());
        } catch (WrongMethodUseException e) {
            e.printStackTrace();
        }
        return false;
    }


}
