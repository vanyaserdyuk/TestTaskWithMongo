package ru.testtask.service;

import org.springframework.stereotype.Component;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectService {

    private final ProjectRepo projectRepo;

    private final UserService userService;

    public ProjectService(ProjectRepo projectRepo, UserService userService) {
        this.projectRepo = projectRepo;
        this.userService = userService;
    }


    public Optional<Project> findProjectById(String id) {
        return projectRepo.findById(id);
    }

    public Project findProjectByName(String name) {
        return projectRepo.findByName(name);
    }


    public Project createProject(Project project) throws NameAlreadyExistsException {
        if (findProjectByName(project.getName()) != null) {
            throw new NameAlreadyExistsException("Project with the same name already exists!");
        } else {
            for (int i = 0; i < 10; i++) {
                project.addGeometry(Geometry.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
                project.addAttribute(Attribute.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
            }
            project.setOwnerId(userService.getCurrentUserId());
            return projectRepo.insert(project);
        }
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

    public boolean isCurrentUserOwnerOf(String id) {
        Project project;

        Optional<Project> optionalProject = findProjectById(id);

        if (optionalProject.isPresent()) {
            project = optionalProject.get();
        } else return false;

        return Objects.equals(userService.getCurrentUserId(), project.getOwnerId());
    }

    public void deleteAllProjects(){
        projectRepo.deleteAll();
    }


}
