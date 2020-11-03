package ru.testtask.converter;

import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.testtask.dto.AttrDTO;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Attribute;
import ru.testtask.model.Project;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class ProjectDTOConverter {

    @Autowired
    private ModelMapper projectModelMapper;

    @Autowired
    private ModelMapper modelMapper;


    public ProjectDTO convertProjectToDTO(Project project){
        this.projectModelMapper.typeMap(Project.class, ProjectDTO.class).addMapping(Project::getAttributes, ProjectDTO::setAttrs);
        return projectModelMapper.map(project, ProjectDTO.class);
    }

    public List<ProjectDTO> getDTOProjectsList(List<Project> projects){
        return projects.stream().map(this::convertProjectToDTO).collect(Collectors.toList());
    }

    public Project convertDTOtoProject(CreateProjectDTO createProjectDTO){
        return modelMapper.map(createProjectDTO, Project.class);
    }

    public List<Attribute> getAttrListFromDTO(List<AttrDTO> attrDTOS){
        return attrDTOS.stream().map(attrDTO -> modelMapper.map(attrDTO, Attribute.class)).collect(Collectors.toList());
    }
}
