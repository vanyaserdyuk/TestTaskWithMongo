package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.converter.ProjectDTOConverter;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDTOConverter projectDtoConverter;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable("id") String id) {
        Optional<Project> project = projectService.findProjectById(id);

        if (project.isEmpty())
            return new ResponseEntity<>("Project with ID " + id + " does not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(project.get()), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable ("name") String name) {
        Project project = projectService.findProjectByName(name);

        if (project == null)
            return new ResponseEntity<>("Project with name " + name + " not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(project), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ProjectDTO> postProject(@RequestBody CreateProjectDTO createProjectDTO) {
        if (createProjectDTO.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Project project = projectDtoConverter.convertDTOtoProject(createProjectDTO);

        projectService.createProject(project);
        return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(project)
                ,HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.findAllProjects();
        List<ProjectDTO> dtos = projectDtoConverter.getDTOProjectsList(projects);

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {

        if (projectService.findProjectById(id).isEmpty()) {
            return new ResponseEntity<>(String.format("Project with ID %s does not found", id), HttpStatus.NOT_FOUND);
        }

        projectService.deleteProject(id);

        return new ResponseEntity<>(String.format("Project with ID %s removed successfully", id), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") String id,
                                                 @RequestBody ProjectDTO projectDTO) {
        if (!projectDTO.getId().equals(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Project> optionalProject = projectService.findProjectById(id);

        if (optionalProject.isPresent()){
            Project project = optionalProject.get();
            project.setName(projectDTO.getName());
            project.setAttributes(projectDtoConverter.getAttrListFromDTO(projectDTO.getAttrs()));
            projectService.updateProject(project);
            return new ResponseEntity<>(project, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
