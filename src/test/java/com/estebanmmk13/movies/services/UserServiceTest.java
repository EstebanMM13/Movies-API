package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

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
    @DisplayName("Debería devolver todos los usuarios")
    void findAll() {
        List<User> users = List.of(user);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.getFirst().getUsername());
    }

    @Test
    @DisplayName("Debería encontrar un usuario por ID")
    void findById() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por ID")
    void findByIdNotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
    }

    @Test
    @DisplayName("Debería crear un usuario nuevo")
    void create() {
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Debería actualizar un usuario existente")
    void update() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(1L, user);

        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar usuario inexistente")
    void updateNotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, user));
    }

    @Test
    @DisplayName("Debería eliminar un usuario existente")
    void delete() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un usuario inexistente")
    void deleteNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("Debería encontrar un usuario por username ignorando mayúsculas")
    void findByUsernameIgnoreCase() {
        Mockito.when(userRepository.findUserByUsernameIgnoreCase("estebanmm13")).thenReturn(Optional.of(user));

        User result = userService.findUserByUsernameIgnoreCase("estebanmm13");

        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por username")
    void findByUsernameNotFound() {
        Mockito.when(userRepository.findUserByUsernameIgnoreCase("noexiste")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsernameIgnoreCase("noexiste"));
    }

    @Test
    @DisplayName("Debería encontrar un usuario por email")
    void findByEmail() {
        Mockito.when(userRepository.findUserByEmail("esteban@gmail.com")).thenReturn(Optional.of(user));

        User result = userService.findUserByEmail("esteban@gmail.com");

        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por email")
    void findByEmailNotFound() {
        Mockito.when(userRepository.findUserByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail("noexiste@gmail.com"));
    }

    @Test
    @DisplayName("Debería devolver true si el email existe")
    void existsByEmailTrue() {
        Mockito.when(userRepository.existsByEmail("esteban@gmail.com")).thenReturn(true);

        boolean exists = userService.existsUserByEmail("esteban@gmail.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Debería devolver false si el email no existe")
    void existsByEmailFalse() {
        Mockito.when(userRepository.existsByEmail("noexiste@gmail.com")).thenReturn(false);

        boolean exists = userService.existsUserByEmail("noexiste@gmail.com");

        assertFalse(exists);
    }


}