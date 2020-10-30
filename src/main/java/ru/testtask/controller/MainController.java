package ru.testtask.controller;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.Project;
import ru.testtask.model.User;
import ru.testtask.model.Role;
import ru.testtask.service.ProjectService;
import ru.testtask.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
public class MainController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable("id") String id) {
        Optional<Project> project = projectService.findProjectById(id);

        if (project.isEmpty())
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable ("name") String name) {
        Project project = projectService.findProjectByName(name);

        if (project == null)
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> postProject(@ModelAttribute("project") Project project) {
        try {
            projectService.createProject(project);
            return new ResponseEntity<>(project, HttpStatus.CREATED);
        }
        catch(NameAlreadyExistsException e){
            return new ResponseEntity<>("Project with the same name already exists!",
                    HttpStatus.CONFLICT);
        }

    }


    @GetMapping()
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAllProjects();

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PreAuthorize("projectService.isCurrentUserOwnerOf(#id) or hasAuthority('MODERATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {

        if (projectService.findProjectById(id).isEmpty()) {
            return new ResponseEntity<>("This project does not exist!", HttpStatus.NOT_FOUND);
        }

        projectService.deleteProject(id);

        return new ResponseEntity<>("Succesfully removed", HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("projectService.isCurrentUserOwnerOf(#id) or hasAuthority('MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") String id, @RequestParam(value = "name") String name) {
        Optional<Project> project = projectService.findProjectById(id);

        if (project.isPresent()){
            Project p = project.get();
            p.setName(name);
            projectService.updateProject(p);
            return new ResponseEntity<>(p, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



}
