package ru.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.testtask.Application;
import ru.testtask.dto.AttrDTO;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
@WithMockUser(authorities = "MODERATOR")
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;


    @Test
    public void postProjectTest() throws Exception {
        CreateProjectDTO createProjectDTO = new CreateProjectDTO();
        createProjectDTO.setName("prj");

        Project project = new Project();
        project.setName("prj");

        Mockito.when(projectService.createProject(Mockito.any())).thenReturn(project);
        project.setId("a");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/projects/")
                        .content(objectMapper.writeValueAsString(createProjectDTO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("prj"))
                .andExpect(authenticated());
    }


    @Test
    public void getProjectByIdTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(Attribute.builder().name("attr").id("id").build());
        project.setAttributes(attributeList);

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.
                        get("/api/projects/a"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("prj"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attrs[0].id").value("id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attrs[0].name").value("attr"));
    }

    @Test
    public void emptyProjectTest() throws Exception {
        Mockito.when(projectService.findProjectById(Mockito.anyString())).
                thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects/a"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putProjectTest() throws Exception {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(Attribute.builder().id("id").name("attr").build());
        Project project = Project.builder().id("a").name("prj1").attributes(attributes)
                .geometries(new ArrayList<>()).build();


        List<AttrDTO> attrDTOS = new ArrayList<>();
        attrDTOS.add(AttrDTO.builder().id("id").name("attr").build());
        ProjectDTO projectDTO = ProjectDTO.builder().id("a").name("prj1").attrs(attrDTOS).build();

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/projects/a")
                        .content(objectMapper.writeValueAsString(projectDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("prj1"));
    }

    @Test
    public void deleteProjectTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/projects/a"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();
        Project project1 = Project.builder().id("b").name("prj1")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(Attribute.builder().name("attr").id("id").build());
        project.setAttributes(attributeList);

        Mockito.when(projectService.findAllProjects()).thenReturn(Arrays.asList(project, project1));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects/"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("prj"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].attrs[0].id").value("id"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].attrs[0].name").value("attr"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("prj1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].attrs.length()", is(0)));
    }
}