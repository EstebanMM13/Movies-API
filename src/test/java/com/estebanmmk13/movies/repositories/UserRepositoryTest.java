package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .username("EstebanMM13")
                .email("esteban@gmail.com")
                .password("1234")
                .build();

        testEntityManager.persist(existingUser);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Debería guardar un usuario correctamente")
    void saveShouldPersistUser() {
        // Given
        User newUser = User.builder()
                .username("NewUser")
                .email("newuser@gmail.com")
                .password("pass")
                .build();

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("NewUser");
        assertThat(saved.getEmail()).isEqualTo("newuser@gmail.com");

        // Verificar en BD
        User found = testEntityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("NewUser");
    }

    @Test
    @DisplayName("Debería encontrar un usuario por username exacto")
    void findByUsernameShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findUserByUsername("EstebanMM13");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("esteban@gmail.com");
    }

    @Test
    @DisplayName("Debería encontrar un usuario por username exacto ignorando mayúsculas")
    void findByUsernameIgnoreCaseShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findUserByUsernameIgnoreCase("estebanmm13");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("EstebanMM13");
    }

    @Test
    @DisplayName("Debería encontrar usuarios por username que contenga el texto")
    void findByUsernameContainingIgnoreCaseShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findUserByUsernameIgnoreCaseContaining("ESTEBAN");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("EstebanMM13");
    }

    @Test
    @DisplayName("Debería devolver vacío si username no existe")
    void findByUsernameShouldReturnEmptyIfNotExists() {
        // When
        Optional<User> result = userRepository.findUserByUsername("inexistente");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debería encontrar un usuario por email")
    void findByEmailShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findUserByEmail("esteban@gmail.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("EstebanMM13");
    }

    @Test
    @DisplayName("Debería encontrar un usuario por email ignorando mayúsculas")
    void findByEmailIgnoreCaseShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findUserByEmailIgnoreCase("ESTEBAN@GMAIL.COM");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("EstebanMM13");
    }

    @Test
    @DisplayName("Debería devolver vacío si el email no existe")
    void findByEmailShouldReturnEmptyIfNotExists() {
        // When
        Optional<User> result = userRepository.findUserByEmail("noexiste@email.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debería devolver true si el email ya existe")
    void existsByEmailShouldReturnTrueIfExists() {
        // When
        boolean exists = userRepository.existsByEmail("esteban@gmail.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debería devolver false si el email no existe")
    void existsByEmailShouldReturnFalseIfNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("otro@email.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debería verificar si un username ya existe")
    void existsByUsernameShouldWork() {
        // When
        boolean exists = userRepository.existsByUsername("EstebanMM13");
        boolean notExists = userRepository.existsByUsername("NoExiste");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Debería devolver todos los usuarios paginados")
    void findAllShouldReturnPagedUsers() {
        // Given - crear más usuarios
        for (int i = 0; i < 15; i++) {
            User user = User.builder()
                    .username("User" + i)
                    .email("user" + i + "@gmail.com")
                    .password("pass")
                    .build();
            testEntityManager.persist(user);
        }
        testEntityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> page = userRepository.findAll(pageable);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(16); // 15 nuevas + 1 del setUp
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debería eliminar un usuario correctamente")
    void deleteByIdShouldRemoveUser() {
        // Given
        User userToDelete = User.builder()
                .username("DeleteMe")
                .email("deleteme@gmail.com")
                .password("pass")
                .build();

        User saved = testEntityManager.persist(userToDelete);
        testEntityManager.flush();

        // When
        userRepository.deleteById(saved.getId());
        testEntityManager.flush();

        // Then
        Optional<User> result = userRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No debería afectar a otros usuarios al eliminar")
    void deleteByIdShouldNotAffectOtherUsers() {
        // Given
        User userToDelete = User.builder()
                .username("DeleteMe")
                .email("deleteme@gmail.com")
                .password("pass")
                .build();

        User saved = testEntityManager.persist(userToDelete);
        testEntityManager.flush();

        // When
        userRepository.deleteById(saved.getId());

        // Then - el usuario original sigue existiendo
        Optional<User> originalUser = userRepository.findById(existingUser.getId());
        assertThat(originalUser).isPresent();
        assertThat(originalUser.get().getUsername()).isEqualTo("EstebanMM13");
    }
}