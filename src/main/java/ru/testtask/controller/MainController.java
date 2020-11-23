package ru.testtask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.testtask.converter.ProjectDTOConverter;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class MainController {

    private final ProjectService projectService;

    private final ProjectDTOConverter projectDtoConverter;

    public MainController(ProjectService projectService, ProjectDTOConverter projectDtoConverter) {
        this.projectService = projectService;
        this.projectDtoConverter = projectDtoConverter;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable("id") String id) {
        Optional<Project> project = projectService.findProjectById(id);

        if (project.isEmpty())
            return new ResponseEntity<>(String.format("Project with ID %s does not found", id), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(project.get()), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable ("name") String name) {
        Project project = projectService.findProjectByName(name);

        if (project == null)
            return new ResponseEntity<>(String.format("Project with name %s does not found", name), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(project), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ProjectDTO> postProject(@RequestBody CreateProjectDTO createProjectDTO) {
        if (createProjectDTO.getName().isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectDtoConverter.convertDTOtoProject(createProjectDTO);

        try {
            return new ResponseEntity<>(projectDtoConverter.convertProjectToDTO(projectService.createProject(project)),
                    HttpStatus.CREATED);
        }
        catch(NameAlreadyExistsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @GetMapping()
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectService.findAllProjects();

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ProjectDTO> dtos = projectDtoConverter.getDTOProjectsList(projects);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('MODERATOR') or projectService.isCurrentUserOwnerOf(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {

        if (projectService.findProjectById(id).isEmpty()) {
            return new ResponseEntity<>(String.format("Project with ID %s does not found", id), HttpStatus.NOT_FOUND);
        }

        projectService.deleteProject(id);

        return new ResponseEntity<>(String.format("Project with ID %s removed successfully", id), HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('MODERATOR') ")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") String id, @RequestBody ProjectDTO projectDTO) {
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




