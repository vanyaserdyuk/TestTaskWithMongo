package ru.testtask.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.dto.AttrDTO;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Attribute;
import ru.testtask.model.Project;
import ru.testtask.service.UserService;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ProjectDTOConverterTest {
    @Autowired
    private ProjectDTOConverter projectDTOConverter;

    @Autowired
    private DTOConverterConfig modelMapper;

    private Project project;
    private ProjectDTO projectDTO;
    private CreateProjectDTO createProjectDTO;

    @Before
    public void buildTestData(){
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.builder().name("attr1").id("a1").build());
        attributes.add(Attribute.builder().name("attr2").id("a2").build());
        List<AttrDTO> attrDTOS = new ArrayList<>();
        attrDTOS.add(AttrDTO.builder().name("attrd1").id("1").build());
        attrDTOS.add(AttrDTO.builder().name("attrd1").id("2").build());

        project = Project.builder().id("a").name("prj").attributes(attributes)
                .geometries(new ArrayList<>()).build();
        projectDTO = ProjectDTO.builder().id("a").name("prj").attrs(attrDTOS).build();
        createProjectDTO = new CreateProjectDTO("prj");
    }

    @Test
    public void convertProjectToDTOTest(){

        ProjectDTO resultProjectDTO = projectDTOConverter.convertProjectToDTO(project);
        assertNotNull(resultProjectDTO);
        assertEquals(project.getId(), resultProjectDTO.getId());
        assertEquals(project.getName(), resultProjectDTO.getName());
        assertEquals(2, resultProjectDTO.getAttrs().size());
        for (int i = 0; i < resultProjectDTO.getAttrs().size(); i++) {
            assertEquals(project.getAttributes().get(i).getId(), resultProjectDTO.getAttrs().get(i).getId());
            assertEquals(project.getAttributes().get(i).getName(), resultProjectDTO.getAttrs().get(i).getName());
        }
    }

    @Test
    public void convertCreateProjectDTOtoProjectTest(){
        Project resultProject = projectDTOConverter.convertDTOtoProject(createProjectDTO);
        assertNotNull(resultProject);
        assertEquals(createProjectDTO.getName(), resultProject.getName());
    }

    @Test
    public void getDTOProjectsListTest(){
        List<Attribute> attributes1 = new ArrayList<>();
        attributes1.add(Attribute.builder().name("attr3").id("a3").build());
        attributes1.add(Attribute.builder().name("attr4").id("a4").build());
        Project project1 = Project.builder().id("b").name("prj1").attributes(attributes1)
                .geometries(new ArrayList<>()).build();



        List<Project> projects = new ArrayList<>();
        projects.add(project);
        projects.add(project1);

        List<ProjectDTO> testList = projectDTOConverter.getDTOProjectsList(projects);
        assertEquals(2, testList.size());
        assertEquals(projects.get(0).getId(), testList.get(0).getId());
        assertEquals(projects.get(0).getName(), testList.get(0).getName());
        assertEquals(projects.get(1).getId(), testList.get(1).getId());
        assertEquals(projects.get(1).getName(), testList.get(1).getName());

        for (int i = 0; i < project.getAttributes().size(); i++) {
            assertEquals(project.getAttributes().get(i).getId(), testList.get(0).getAttrs().get(i).getId());
            assertEquals(project.getAttributes().get(i).getName(), testList.get(0).getAttrs().get(i).getName());
            assertEquals(project1.getAttributes().get(i).getId(), testList.get(1).getAttrs().get(i).getId());
            assertEquals(project1.getAttributes().get(i).getName(), testList.get(1).getAttrs().get(i).getName());
        }
    }

    @Test
    public void getAttrListFromDTOTest(){
        AttrDTO attrDTO = new AttrDTO("a", "attr");
        AttrDTO attrDTO1 = new AttrDTO("b", "attr1");
        List<AttrDTO> attrDTOS = new ArrayList<>();
        attrDTOS.add(attrDTO);
        attrDTOS.add(attrDTO1);

        List<Attribute> testList = projectDTOConverter.getAttrListFromDTO(attrDTOS);
        assertEquals(2, testList.size());

        assertEquals(attrDTO.getId(), testList.get(0).getId());
        assertEquals(attrDTO.getName(), testList.get(0).getName());
        assertEquals(attrDTO1.getId(), testList.get(1).getId());
        assertEquals(attrDTO1.getName(), testList.get(1).getName());
    }


}
