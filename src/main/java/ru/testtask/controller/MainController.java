package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class MainController {

    @Autowired
    private ProjectService projectService;

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
    public ResponseEntity<Project> postProject(@ModelAttribute("project") Project project) {
        if (project.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        projectService.createProject(project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAllProjects();

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {

        if (projectService.findProjectById(id).isEmpty()) {
            return new ResponseEntity<>("This project does not exist!", HttpStatus.NOT_FOUND);
        }

        projectService.deleteProject(id);

        return new ResponseEntity<>("Succesfully removed", HttpStatus.NO_CONTENT);
    }


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
