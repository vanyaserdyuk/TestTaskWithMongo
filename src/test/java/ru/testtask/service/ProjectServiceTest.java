package ru.testtask.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.config.TestConfig;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;

import java.util.List;

import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private Project testProject;

    @Before
    public void buildTestProject(){
        testProject = new Project();
        testProject.setName("TestProject");

    }

    @Before
    public void clearDb(){
        projectService.deleteAllProjects();
    }

    @Test
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
        projectService.createProject(testProject);
        projectService.createProject(testProject);
    }

    @Test
    public void isCurrentUserOwnerOfTest(){
        Mockito.when(userService.getCurrentUserId()).thenReturn("a");
        projectService.createProject(testProject);
        testProject.setOwnerId("a");
        assertTrue(projectService.isCurrentUserOwnerOf(testProject.getId()));
    }
}
