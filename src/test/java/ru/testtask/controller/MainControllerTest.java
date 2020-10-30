package ru.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;





    @Test
    public void postProjectTest() throws Exception {
        Project project = Project.builder().id("a").name("prj").
        attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.createProject(Mockito.any())).thenReturn(project);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/projects/")
                        .content(objectMapper.writeValueAsString(project))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(project)));
    }


    @Test
    public void getProjectByIdTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.
                        get("/api/projects/a"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("prj"));
    }

    @Test
    public void emptyProjectTest() throws Exception {
        Mockito.when(projectService.findProjectById(Mockito.anyString())).
                thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/projects/a"))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(EntityNotFoundException.class));
    }
    @Test
    public void putProjectTest() throws Exception {
        Project project = Project.builder().id("a").name("prj").build();

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.put("/projects/")
                        .content(objectMapper.writeValueAsString(Project.builder().id("a").name("prj")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("prj"));
    }

    @Test
    public void deleteProjectTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/projects/a"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();
        Project project1 = Project.builder().id("b").name("prj1")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.findAllProjects()).thenReturn(Arrays.asList(project, project1));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/projects/"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(project, project1))));

    }
}
