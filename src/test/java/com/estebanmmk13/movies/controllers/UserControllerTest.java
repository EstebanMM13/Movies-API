package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = "USER")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User user;
    private User updatedUser;
    private Pageable pageable;
    private Page<User> userPage;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("1234")
                .role(Role.USER)
                .build();

        updatedUser = User.builder()
                .id(1L)
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("newpassword123")
                .role(Role.USER)
                .build();

        pageable = PageRequest.of(0, 10);
        userPage = new PageImpl<>(List.of(user), pageable, 1);
    }

    @Test
    @DisplayName("GET /api/users - Should return paginated list of users")
    void findAllUsers_ShouldReturnPageOfUsers() throws Exception {
        when(userService.findAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("EstebanMM13"))
                .andExpect(jsonPath("$.content[0].email").value("esteban@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user when exists")
    void findUserById_WhenExists_ShouldReturnUser() throws Exception {
        when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"));

        verify(userService).findUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 when user not found")
    void findUserById_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserById(99L))
                .thenThrow(new UserNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService).findUserById(99L);
    }

    @Test
    @DisplayName("POST /api/users - Should create user and return 201")
    void createUser_ShouldReturnCreated() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "EstebanMM13",
                                    "email": "esteban@gmail.com",
                                    "password": "1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Should update user and return 200")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "password": "newpassword123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.password").value("newpassword123"));

        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Should return 404 when user not found")
    void updateUser_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.updateUser(eq(99L), any(User.class)))
                .thenThrow(new UserNotFoundException("User not found with id: 99"));

        mockMvc.perform(patch("/api/users/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "NuevoNombre"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(99L), any(User.class));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Should allow partial update")
    void updateUser_WithPartialData_ShouldUpdateOnlyProvidedFields() throws Exception {
        User partiallyUpdated = User.builder()
                .id(1L)
                .username("NuevoUsername")
                .email("esteban@gmail.com")  // Se mantiene igual
                .password("1234")  // Se mantiene igual
                .role(Role.USER)
                .build();

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(partiallyUpdated);

        mockMvc.perform(patch("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "NuevoUsername"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("NuevoUsername"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user and return 204")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 404 when user not found")
    void deleteUser_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new UserNotFoundException("User not found with id: 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(99L);
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Should return user by username")
    void findUserByUsername_ShouldReturnUser() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("EstebanMM13")).thenReturn(user);

        mockMvc.perform(get("/api/users/username/EstebanMM13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"));

        verify(userService).findUserByUsernameIgnoreCase("EstebanMM13");
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Should return 404 when username not found")
    void findUserByUsername_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("NoExiste"))
                .thenThrow(new UserNotFoundException("User not found with username: NoExiste"));

        mockMvc.perform(get("/api/users/username/NoExiste"))
                .andExpect(status().isNotFound());

        verify(userService).findUserByUsernameIgnoreCase("NoExiste");
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Should be case insensitive")
    void findUserByUsername_ShouldBeCaseInsensitive() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("estebanmm13")).thenReturn(user);

        mockMvc.perform(get("/api/users/username/estebanmm13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Should return user by email")
    void findUserByEmail_ShouldReturnUser() throws Exception {
        when(userService.findUserByEmail("esteban@gmail.com")).thenReturn(user);

        mockMvc.perform(get("/api/users/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.username").value("EstebanMM13"));

        verify(userService).findUserByEmail("esteban@gmail.com");
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Should return 404 when email not found")
    void findUserByEmail_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserByEmail("noexiste@gmail.com"))
                .thenThrow(new UserNotFoundException("User not found with email: noexiste@gmail.com"));

        mockMvc.perform(get("/api/users/email/noexiste@gmail.com"))
                .andExpect(status().isNotFound());

        verify(userService).findUserByEmail("noexiste@gmail.com");
    }

    @Test
    @DisplayName("GET /api/users/exists/email/{email} - Should return true when email exists")
    void existsUserByEmail_WhenExists_ShouldReturnTrue() throws Exception {
        when(userService.existsUserByEmail("esteban@gmail.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/exists/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService).existsUserByEmail("esteban@gmail.com");
    }

    @Test
    @DisplayName("GET /api/users/exists/email/{email} - Should return false when email does not exist")
    void existsUserByEmail_WhenNotExists_ShouldReturnFalse() throws Exception {
        when(userService.existsUserByEmail("noexiste@gmail.com")).thenReturn(false);

        mockMvc.perform(get("/api/users/exists/email/noexiste@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(userService).existsUserByEmail("noexiste@gmail.com");
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly in findAllUsers")
    void findAllUsers_ShouldHandlePaginationParameters() throws Exception {
        when(userService.findAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "username,desc"))
                .andExpect(status().isOk());

        verify(userService).findAllUsers(argThat(p ->
                p.getPageNumber() == 2 &&
                        p.getPageSize() == 5 &&
                        p.getSort().toString().contains("username: DESC")
        ));
    }

}