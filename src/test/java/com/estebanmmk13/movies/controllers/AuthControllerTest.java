package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.request.AuthenticationRequest;
import com.estebanmmk13.movies.dtoModels.response.AuthResponse;
import com.estebanmmk13.movies.dtoModels.request.RegisterRequest;
import com.estebanmmk13.movies.error.DuplicateResourceException;
import com.estebanmmk13.movies.error.InvalidCredentialsException;
import com.estebanmmk13.movies.services.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final String TEST_TOKEN = "jwt-token-123";

    // ========== REGISTER ENDPOINT TESTS ==========

    @Test
    @DisplayName("POST /api/auth/register - Debería registrar usuario y devolver 201 con token")
    void register_ShouldReturnCreatedAndToken() throws Exception {
        RegisterRequest request = new RegisterRequest("nuevo", "nuevo@test.com", "password123");
        AuthResponse response = new AuthResponse(TEST_TOKEN);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN));
    }

    @Test
    @DisplayName("POST /api/auth/register - Debería devolver 400 si faltan campos")
    void register_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "not-an-email", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Debería devolver 409 si email ya existe")
    void register_WhenEmailExists_ShouldReturnConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("nuevo", "existente@test.com", "pass123");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("User with email existente@test.com already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ========== AUTHENTICATE ENDPOINT TESTS ==========

    @Test
    @DisplayName("POST /api/auth/authenticate - Debería autenticar y devolver 200 con token")
    void authenticate_WithValidCredentials_ShouldReturnOkAndToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("usuario", "password");
        AuthResponse response = new AuthResponse(TEST_TOKEN);

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TEST_TOKEN));
    }

    @Test
    @DisplayName("POST /api/auth/authenticate - Debería devolver 401 si credenciales inválidas")
    void authenticate_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("usuario", "wrong");
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new InvalidCredentialsException("User or password incorrect"));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/authenticate - Debería devolver 400 si faltan campos")
    void authenticate_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        AuthenticationRequest invalidRequest = new AuthenticationRequest("", "");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}