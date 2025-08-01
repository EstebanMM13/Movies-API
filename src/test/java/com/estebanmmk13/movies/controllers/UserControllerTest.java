package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("1234")
                .build();
    }

    @Test
    void findAllUsers() throws Exception {
        Mockito.when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("EstebanMM13"));
    }

    @Test
    void findUserById() throws Exception {
        Mockito.when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"));
    }

    @Test
    void findUserByIdNotFound() throws Exception {
        Mockito.when(userService.findUserById(99L)).thenThrow(new UserNotFoundException("No encontrada"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findUserByUsername() throws Exception {
        Mockito.when(userService.findUserByUsernameIgnoreCase("EstebanMM13")).thenReturn(user);

        mockMvc.perform(get("/api/users/username/EstebanMM13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"));
    }

    @Test
    void findUserByUsernameNotFound() throws Exception {
        Mockito.when(userService.findUserByUsernameIgnoreCase("NoExiste")).thenThrow(new UserNotFoundException("No encontrada"));

        mockMvc.perform(get("/api/users/username/NoExiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findUserByEmail() throws Exception {
        Mockito.when(userService.findUserByEmail("esteban@gmail.com")).thenReturn(user);

        mockMvc.perform(get("/api/users/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"));
    }

    @Test
    void existsUserByEmail() throws Exception {
        Mockito.when(userService.existsUserByEmail("esteban@gmail.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/exists/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void createUser() throws Exception {
        Mockito.when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "EstebanMM13",
                                    "email": "esteban@gmail.com",
                                    "password": "1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("EstebanMM13"));
    }

    @Test
    void updateUser() throws Exception {
        User updated = User.builder()
                .id(1L)
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("newpassword")
                .build();

        Mockito.when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "EstebanMM13",
                                    "email": "esteban@gmail.com",
                                    "password": "newpassword"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("newpassword"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        Mockito.when(userService.updateUser(eq(99L), any(User.class)))
                .thenThrow(new UserNotFoundException("No encontrada"));

        mockMvc.perform(patch("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "NoExiste",
                                    "email": "noexiste@gmail.com",
                                    "password": "1234"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        Mockito.doThrow(new UserNotFoundException("No encontrada")).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}

