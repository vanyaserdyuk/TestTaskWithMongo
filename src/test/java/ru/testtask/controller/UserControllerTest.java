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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.testtask.model.Role;
import ru.testtask.model.User;
import ru.testtask.repo.ProjectRepo;
import ru.testtask.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ADMIN")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ProjectRepo projectRepo;


    @Test
    public void postUserTest() throws Exception {
        User user = new User();
        user.setUsername("testUser");

        Mockito.when(userService.createUser(Mockito.any())).thenReturn(user);
        user.setId("a");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());
    }


    @Test
    public void getUserByIdTest() throws Exception {
        User user = User.builder().id("a").username("user")
                .roles(Collections.singleton(Role.USER)).build();


        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(
                MockMvcRequestBuilders.
                        get("/api/users/a"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").value("USER"));
    }

    @Test
    public void emptyUserTest() throws Exception {
        Mockito.when(userService.getUserById(Mockito.anyString())).
                thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/a"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putUserTest() throws Exception {
        User user = User.builder().id("a").username("testUser").roles(Collections.singleton(Role.USER)).build();
        String name = "testUser";

        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/users/a")
                        .content(objectMapper.writeValueAsString("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").value("USER"));
    }

    @Test
    public void deleteUserTest() throws Exception {
        User user = User.builder().id("a").username("user")
                .roles(Collections.singleton(Role.USER)).build();

        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/users/a"))
                .andExpect(status().isNoContent())
                .andExpect(authenticated());
    }


    @Test
    public void getAllUsersTest() throws Exception {
        User user = User.builder().id("a").username("testUser1")
                .roles(Collections.singleton(Role.USER)).build();

        User admin = User.builder().id("b").username("testUser2")
                .roles(Collections.singleton(Role.ADMIN)).build();

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user, admin));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").value("a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.roles").value("USER"));

    }
}
