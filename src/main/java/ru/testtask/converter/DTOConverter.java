package ru.testtask.converter;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.testtask.dto.AttrDTO;
import ru.testtask.dto.ClientProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Attribute;
import ru.testtask.model.Project;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class DTOConverter {

    public ProjectDTO convertToDTO(Project project){
       List<AttrDTO> attrs = getDTOAttrList(project.getAttributes());
       return ProjectDTO.builder().id(project.getId()).name(project.getName()).attrs(attrs).build();
    }

    public List<AttrDTO> getDTOAttrList(List<Attribute> attributes){
        return attributes.stream().map(attribute -> convertAttrToDTO(attribute)).collect(Collectors.toList());
    }

    public List<ProjectDTO> getDTOProjectsList(List<Project> projects){
        return projects.stream().map(project -> convertToDTO(project)).collect(Collectors.toList());
    }

    public AttrDTO convertAttrToDTO(Attribute attribute){
        return AttrDTO.builder().id(attribute.getId()).name(attribute.getName()).build();
    }

    public Project convertDTOtoProject(ClientProjectDTO clientProjectDTO){
        Project project = new Project();
        project.setName(clientProjectDTO.getName());
        return project;
    }

    public List<Attribute> getAttrListFromDTO(List<AttrDTO> attrDTOS){
        return attrDTOS.stream().map(attrDTO -> convertDTOtoAttr(attrDTO)).collect(Collectors.toList());
    }

    public Attribute convertDTOtoAttr(AttrDTO attrDTO){
        return Attribute.builder().id(attrDTO.getId()).name(attrDTO.getName()).build();
    }

}
