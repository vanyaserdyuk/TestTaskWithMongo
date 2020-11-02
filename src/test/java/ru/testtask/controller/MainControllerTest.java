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
import ru.testtask.repo.ProjectRepo;
import ru.testtask.service.ProjectService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectRepo projectRepo;





    @Test
    public void postProjectTest() throws Exception {
        Project project = new Project();
        project.setName("prj");

        Mockito.when(projectService.createProject(Mockito.any())).thenReturn(project);
        project.setId("a");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/projects/")
                        .content(objectMapper.writeValueAsString(project))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());
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
                MockMvcRequestBuilders.get("/api/projects/a"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putProjectTest() throws Exception {
        Project project = Project.builder().id("a").name("prj1").build();
        String name = "prj";

        Mockito.when(projectService.findProjectById(Mockito.anyString())).thenReturn(Optional.of(project));
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/projects/a")
                        .content(objectMapper.writeValueAsString("prj"))
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
                MockMvcRequestBuilders.delete("/api/projects/a"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        Project project = Project.builder().id("a").name("prj")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();
        Project project1 = Project.builder().id("b").name("prj1")
                .attributes(new ArrayList<>()).geometries(new ArrayList<>()).build();

        Mockito.when(projectService.findAllProjects()).thenReturn(Arrays.asList(project, project1));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/projects/"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(project, project1))));

    }
}
