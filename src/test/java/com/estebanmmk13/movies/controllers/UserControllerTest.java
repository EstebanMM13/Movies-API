package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.request.UserRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.UserResponseDTO;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;  // ← AÑADIR ESTO

    private UserResponseDTO userResponseDTO;
    private UserResponseDTO updatedUserResponseDTO;
    private UserRequestDTO userRequestDTO;
    private Page<UserResponseDTO> userPage;
    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        userResponseDTO = new UserResponseDTO(1L, "EstebanMM13", "esteban@gmail.com", "USER");
        updatedUserResponseDTO = new UserResponseDTO(1L, "EstebanMM13", "esteban@gmail.com", "USER");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("EstebanMM13");
        userRequestDTO.setEmail("esteban@gmail.com");
        userRequestDTO.setPassword("1234");
        userRequestDTO.setRole("USER");

        Pageable pageable = PageRequest.of(0, 10);
        userPage = new PageImpl<>(List.of(userResponseDTO), pageable, 1);

        adminUser = User.builder()
                .id(999L)
                .username("admin")
                .email("admin@test.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        normalUser = User.builder()
                .id(1L)
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    // ========== GET /api/users ==========
    @Test
    @DisplayName("GET /api/users - Should return paginated list of UserResponseDTO")
    @WithMockUser(roles = "ADMIN")
    void findAllUsers_ShouldReturnPageOfUserResponseDTO() throws Exception {
        when(userService.findAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("EstebanMM13"))
                .andExpect(jsonPath("$.content[0].email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.content[0].role").value("USER"))
                .andExpect(jsonPath("$.content[0].password").doesNotExist());

        verify(userService).findAllUsers(any(Pageable.class));
    }

    // ========== GET /api/users/{id} ==========
    @Test
    @DisplayName("GET /api/users/{id} - Should return UserResponseDTO when exists")
    @WithMockUser(roles = "USER")
    void findUserById_WhenExists_ShouldReturnUserResponseDTO() throws Exception {
        when(userService.findUserById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).findUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 when user not found")
    @WithMockUser(roles = "USER")
    void findUserById_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserById(99L))
                .thenThrow(new UserNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService).findUserById(99L);
    }

    // ========== POST /api/users ==========
    @Test
    @DisplayName("POST /api/users - Should create user and return 201 with UserResponseDTO")
    @WithMockUser(roles = "USER")
    void createUser_ShouldReturnCreated() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when validation fails")
    @WithMockUser(roles = "USER")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO();
        invalidDTO.setUsername("");
        invalidDTO.setEmail("not-an-email");
        invalidDTO.setPassword("");

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    // ========== PATCH /api/users/{id} ==========
    @Test
    @DisplayName("PATCH /api/users/{id} - Admin can update any user")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_AsAdmin_ShouldReturnUpdatedUserResponseDTO() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class)))
                .thenReturn(updatedUserResponseDTO);

        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setUsername("NuevoUsername");
        updateDTO.setEmail("nuevo@email.com");
        updateDTO.setPassword("newpass");
        updateDTO.setRole("ADMIN");

        mockMvc.perform(patch("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).updateUser(eq(1L), any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - User can update themselves")
    @WithMockUser(username = "EstebanMM13", roles = "USER")
    void updateUser_AsSameUser_ShouldReturnUpdatedUserResponseDTO() throws Exception {
        when(userService.getCurrentUser()).thenReturn(normalUser);
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class)))
                .thenReturn(updatedUserResponseDTO);

        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setUsername("NuevoUsername");
        updateDTO.setEmail("nuevo@email.com");
        updateDTO.setPassword("newpass");
        updateDTO.setRole("USER");

        mockMvc.perform(patch("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).updateUser(eq(1L), any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - User cannot update another user")
    @WithMockUser(username = "otheruser", roles = "USER")
    void updateUser_AsDifferentUser_ShouldReturnForbidden() throws Exception {
        User otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .email("other@test.com")
                .role(Role.USER)
                .build();

        when(userService.getCurrentUser()).thenReturn(otherUser);

        mockMvc.perform(patch("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Should return 404 when user not found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(userService.updateUser(eq(99L), any(UserRequestDTO.class)))
                .thenThrow(new UserNotFoundException("User not found with id: 99"));

        mockMvc.perform(patch("/api/users/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(99L), any(UserRequestDTO.class));
    }

    // ========== DELETE /api/users/{id} ==========
    @Test
    @DisplayName("DELETE /api/users/{id} - Admin can delete any user")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_AsAdmin_ShouldReturnNoContent() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - User can delete themselves")
    @WithMockUser(username = "EstebanMM13", roles = "USER")
    void deleteUser_AsSameUser_ShouldReturnNoContent() throws Exception {
        when(userService.getCurrentUser()).thenReturn(normalUser);
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - User cannot delete another user")
    @WithMockUser(username = "otheruser", roles = "USER")
    void deleteUser_AsDifferentUser_ShouldReturnForbidden() throws Exception {
        User otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .email("other@test.com")
                .role(Role.USER)
                .build();

        when(userService.getCurrentUser()).thenReturn(otherUser);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 404 when user not found")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        doThrow(new UserNotFoundException("User not found with id: 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(99L);
    }

    // ========== GET /api/users/username/{username} ==========
    @Test
    @DisplayName("GET /api/users/username/{username} - Should return UserResponseDTO")
    @WithMockUser(roles = "USER")
    void findUserByUsername_ShouldReturnUserResponseDTO() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("EstebanMM13")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/username/EstebanMM13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).findUserByUsernameIgnoreCase("EstebanMM13");
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Should return 404 when not found")
    @WithMockUser(roles = "USER")
    void findUserByUsername_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("NoExiste"))
                .thenThrow(new UserNotFoundException("User not found with username: NoExiste"));

        mockMvc.perform(get("/api/users/username/NoExiste"))
                .andExpect(status().isNotFound());

        verify(userService).findUserByUsernameIgnoreCase("NoExiste");
    }

    @Test
    @DisplayName("GET /api/users/username/{username} - Should be case insensitive")
    @WithMockUser(roles = "USER")
    void findUserByUsername_ShouldBeCaseInsensitive() throws Exception {
        when(userService.findUserByUsernameIgnoreCase("estebanmm13")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/username/estebanmm13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EstebanMM13"));
    }

    // ========== GET /api/users/email/{email} ==========
    @Test
    @DisplayName("GET /api/users/email/{email} - Should return UserResponseDTO")
    @WithMockUser(roles = "USER")
    void findUserByEmail_ShouldReturnUserResponseDTO() throws Exception {
        when(userService.findUserByEmail("esteban@gmail.com")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("esteban@gmail.com"))
                .andExpect(jsonPath("$.username").value("EstebanMM13"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).findUserByEmail("esteban@gmail.com");
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Should return 404 when not found")
    @WithMockUser(roles = "USER")
    void findUserByEmail_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.findUserByEmail("noexiste@gmail.com"))
                .thenThrow(new UserNotFoundException("User not found with email: noexiste@gmail.com"));

        mockMvc.perform(get("/api/users/email/noexiste@gmail.com"))
                .andExpect(status().isNotFound());

        verify(userService).findUserByEmail("noexiste@gmail.com");
    }

    // ========== GET /api/users/exists/email/{email} ==========
    @Test
    @DisplayName("GET /api/users/exists/email/{email} - Should return true if exists")
    @WithMockUser(roles = "USER")
    void existsUserByEmail_WhenExists_ShouldReturnTrue() throws Exception {
        when(userService.existsUserByEmail("esteban@gmail.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/exists/email/esteban@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService).existsUserByEmail("esteban@gmail.com");
    }

    @Test
    @DisplayName("GET /api/users/exists/email/{email} - Should return false if not exists")
    @WithMockUser(roles = "USER")
    void existsUserByEmail_WhenNotExists_ShouldReturnFalse() throws Exception {
        when(userService.existsUserByEmail("noexiste@gmail.com")).thenReturn(false);

        mockMvc.perform(get("/api/users/exists/email/noexiste@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(userService).existsUserByEmail("noexiste@gmail.com");
    }

    // ========== PAGINATION ==========
    @Test
    @DisplayName("Should pass pagination parameters correctly")
    @WithMockUser(roles = "ADMIN")
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