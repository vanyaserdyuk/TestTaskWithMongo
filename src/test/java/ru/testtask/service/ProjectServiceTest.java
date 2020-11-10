package ru.testtask.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.exception.WrongMethodUseException;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;
import ru.testtask.service.ProjectService;
import ru.testtask.service.UserService;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private Project testProject;

    @Autowired
    private ProjectRepo projectRepo;

    @Before
    public void buildTestProject(){
        testProject = new Project();
        testProject.setName("TestProject");
    }

    @Test()
    public void createTest(){
        Project resultProject = projectService.createProject(testProject);
        assertNotNull(resultProject);
        assertEquals(10, resultProject.getAttributes().size());
        assertEquals(10, resultProject.getGeometries().size());
        List<Geometry> testGem = resultProject.getGeometries();
        for (Geometry geometry : testGem){
            assertNotNull(geometry.getName());
            assertNotNull(geometry.getId());
        }
        List<Attribute> testAttr = resultProject.getAttributes();
        for (Attribute attribute : testAttr){
            assertNotNull(attribute.getName());
            assertNotNull(attribute.getId());
        }
    }

    @Test(expected = NameAlreadyExistsException.class)
    public void checkSimilarNamesCreationTest(){

        Project resultProject = projectService.createProject(testProject);
        Project resultProject2 = projectService.createProject(testProject);
    }
}
