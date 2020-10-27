package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.testtask.models.Project;
import ru.testtask.services.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/api/projects")
public class MainController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<String> getProjectById(@PathVariable("id") int id) {
        Project project = projectService.findProject(id);

        if (project == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("name = " + project.getName() + " id = " + project.getId(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Project> postProject(@RequestBody String projectName) {
        String name = projectName.substring(0, projectName.length() - 1);
        Project project = new Project(name);

        if (projectName == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        projectService.saveProject(project);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<String> getAllProjects() {
        List<Project> projects = projectService.findAllUsers();

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        StringBuilder sb = new StringBuilder();

        for (Project project : projects)
            sb.append("id = ").append(project.getId()).append(" , name = ")
                    .append(project.getName()).append("\n");

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Project> deleteProject(@PathVariable("id") int id) {
        Project project = projectService.findProject(id);

        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        projectService.deleteProject(project);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") int id, @RequestBody String projectName) {
        if (projectName == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findProject(id);
        project.setName(projectName);
        projectService.updateProject(project);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }
}
