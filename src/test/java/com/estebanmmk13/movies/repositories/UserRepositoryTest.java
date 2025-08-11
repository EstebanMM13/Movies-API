package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.User;
import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("1234")
                .build();

        testEntityManager.persist(user);
    }

    @Test
    @DisplayName("Debería guardar un usuario correctamente")
    void saveShouldPersistUser() {
        User user = User.builder()
                .username("NewUser")
                .email("newuser@gmail.com")
                .password("pass")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("NewUser", saved.getUsername());
    }

    @Test
    @DisplayName("Debería encontrar un usuario por username ignorando mayúsculas")
    void findByUsernameIgnoreCaseShouldReturnUser() {
        Optional<User> result = userRepository.findUserByUsernameIgnoreCaseContaining("estebanmm13");

        assertTrue(result.isPresent());
        assertEquals("EstebanMM13", result.get().getUsername());
    }

    @Test
    @DisplayName("Debería devolver vacío si username no existe")
    void findByUsernameIgnoreCaseShouldReturnEmptyIfNotExists() {
        Optional<User> result = userRepository.findUserByUsernameIgnoreCaseContaining("inexistente");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería encontrar un usuario por email")
    void findByEmailShouldReturnUser() {
        Optional<User> result = userRepository.findUserByEmail("esteban@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("EstebanMM13", result.get().getUsername());
    }

    @Test
    @DisplayName("Debería devolver vacío si el email no existe")
    void findByEmailShouldReturnEmptyIfNotExists() {
        Optional<User> result = userRepository.findUserByEmail("noexiste@email.com");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería devolver true si el email ya existe")
    void existsByEmailShouldReturnTrueIfExists() {
        boolean exists = userRepository.existsByEmail("esteban@gmail.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Debería devolver false si el email no existe")
    void existsByEmailShouldReturnFalseIfNotExists() {
        boolean exists = userRepository.existsByEmail("otro@email.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Debería devolver todos los usuarios")
    void findAllShouldReturnUsers() {
        User user = User.builder()
                .username("User2")
                .email("user2@gmail.com")
                .password("pass")
                .build();

        testEntityManager.persist(user);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size()); // Incluye el del @BeforeEach
    }

    @Test
    @DisplayName("Debería eliminar un usuario correctamente")
    void deleteByIdShouldRemoveUser() {
        User user = User.builder()
                .username("DeleteMe")
                .email("deleteme@gmail.com")
                .password("pass")
                .build();

        User saved = testEntityManager.persist(user);

        userRepository.deleteById(saved.getId());

        Optional<User> result = userRepository.findById(saved.getId());

        assertTrue(result.isEmpty());
    }
}