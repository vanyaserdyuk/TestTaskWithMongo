package ru.testtask.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import ru.testtask.converter.ProjectDTOConverter;
import ru.testtask.dto.AttrDTO;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Attribute;
import ru.testtask.model.Project;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class ProjectDTOConverterTest {

    private static ProjectDTOConverter projectDTOConverter;

    private static Project project;
    private static ProjectDTO projectDTO;
    private static CreateProjectDTO createProjectDTO;

    @Before
    public void buildTestData(){
        project = Project.builder().id("a").name("prj").attributes(new ArrayList<Attribute>())
                .geometries(new ArrayList<>()).build();
        projectDTO = ProjectDTO.builder().id("a").name("prj").attrs(new ArrayList<AttrDTO>()).build();
        createProjectDTO = new CreateProjectDTO("prj");
    }

    @Before
    public void initConverter(){
        projectDTOConverter = new ProjectDTOConverter();
    }

    @Test
    public void convertProjectToDTOTest(){
        int i = 0;
        ProjectDTO resultProjectDTO = projectDTOConverter.convertProjectToDTO(project);
        assertNotNull(resultProjectDTO);
        assertEquals(resultProjectDTO.getId(), projectDTO.getId());
        assertEquals(resultProjectDTO.getName(), projectDTO.getName());
        while (i < projectDTO.getAttrs().size()){
            assertEquals(projectDTO.getAttrs().get(i), resultProjectDTO.getAttrs().get(i));
            i++;
        }
    }

    @Test
    public void convertDTOtoProjectTest(){
        Project resultProject = projectDTOConverter.convertDTOtoProject(createProjectDTO);
        assertNotNull(resultProject);
        assertEquals(resultProject.getName(), project.getName());
        assertNotNull(resultProject.getId());
        assertNotNull(resultProject.getGeometries());
        assertNotNull(resultProject.getAttributes());
    }

    @Test
    public void getDTOProjectsListTest(){
        Project project1 = Project.builder().id("b").name("prj1").attributes(new ArrayList<Attribute>())
                .geometries(new ArrayList<>()).build();
        ProjectDTO projectDTO1 = ProjectDTO.builder().id("b").name("prj1").attrs(new ArrayList<AttrDTO>()).build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        projects.add(project1);

        List<ProjectDTO> testList = projectDTOConverter.getDTOProjectsList(projects);
        assertEquals(testList.size(), 2);
        assertEquals(testList.get(0).getId(), projectDTO.getId());
        assertEquals(testList.get(0).getName(), projectDTO.getName());
        assertEquals(testList.get(1).getId(), projectDTO1.getId());
        assertEquals(testList.get(1).getName(), projectDTO1.getName());
    }

    @Test
    public void getAttrListFromDTOTest(){
        AttrDTO attrDTO = new AttrDTO("a", "attr");
        AttrDTO attrDTO1 = new AttrDTO("b", "attr1");
        Attribute attribute = new Attribute("a", "attr");
        Attribute attribute1 = new Attribute("b", "attr1");
        List<AttrDTO> attrDTOS = new ArrayList<>();
        attrDTOS.add(attrDTO);
        attrDTOS.add(attrDTO1);

        List<Attribute> testList = projectDTOConverter.getAttrListFromDTO(attrDTOS);
        assertEquals(testList.size(), 2);

        assertEquals(testList.get(0).getId(), attribute.getId());
        assertEquals(testList.get(0).getName(), attribute.getName());
        assertEquals(testList.get(1).getId(), attribute1.getId());
        assertEquals(testList.get(1).getName(), attribute1.getName());
    }


}
