package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.dtoModels.request.AuthenticationRequest;
import com.estebanmmk13.movies.dtoModels.response.AuthResponse;
import com.estebanmmk13.movies.dtoModels.request.RegisterRequest;
import com.estebanmmk13.movies.error.DuplicateResourceException;
import com.estebanmmk13.movies.error.InvalidCredentialsException;
import com.estebanmmk13.movies.error.ResourceNotFoundException;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.config.JwtService;
import com.estebanmmk13.movies.services.auth.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String TEST_TOKEN = "jwt-token-123";

    // ========== REGISTER TESTS ==========

    @Test
    @DisplayName("Debería registrar un nuevo usuario y devolver token")
    void register_ShouldSaveUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("nuevoUser", "nuevo@email.com", "password123");
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_TOKEN);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(TEST_TOKEN);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el email ya existe al registrar")
    void register_WhenEmailExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("nuevoUser", "existente@email.com", "pass");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el username ya existe al registrar")
    void register_WhenUsernameExists_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest("existenteUser", "nuevo@email.com", "pass");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    // ========== AUTHENTICATE TESTS ==========

    @Test
    @DisplayName("Debería autenticar usuario correctamente y devolver token")
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        AuthenticationRequest request = new AuthenticationRequest("usuario", "password");
        User user = User.builder()
                .username(request.getUsername())
                .email("user@email.com")
                .password("encoded")
                .role(Role.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // no lanza excepción
        when(userRepository.findUserByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(TEST_TOKEN);

        AuthResponse response = authService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(TEST_TOKEN);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
    }

    @Test
    @DisplayName("Debería lanzar excepción si las credenciales son inválidas")
    void authenticate_WithInvalidCredentials_ShouldThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("usuario", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("incorrect");

        verify(userRepository, never()).findUserByUsername(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe después de autenticación exitosa (caso improbable)")
    void authenticate_WhenUserNotFoundAfterAuth_ShouldThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("usuario", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findUserByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(ResourceNotFoundException.class)  // ← Cambiado
                .hasMessageContaining("User not found");
    }
}
