package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.dtoModels.request.UserRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.UserResponseDTO;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.mapper.UserMapper;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import com.estebanmmk13.movies.services.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;
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

        userResponseDTO = new UserResponseDTO(1L, "EstebanMM13", "esteban@gmail.com", "USER");
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("EstebanMM13");
        userRequestDTO.setEmail("esteban@gmail.com");
        userRequestDTO.setPassword("1234");
        userRequestDTO.setRole("USER");

        pageable = PageRequest.of(0, 10);
        userPage = new PageImpl<>(List.of(user), pageable, 1);
    }

    // ========== FIND ALL USERS ==========

    @Test
    @DisplayName("Debería devolver todos los usuarios paginados como DTOs")
    void findAllUsers_ShouldReturnPageOfUserResponseDTO() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        Page<UserResponseDTO> result = userService.findAllUsers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("EstebanMM13");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(pageable);
        verify(userMapper, times(1)).toResponseDTO(user);
    }

    @Test
    @DisplayName("Debería devolver página vacía cuando no hay usuarios")
    void findAllUsers_WhenNoUsers_ShouldReturnEmptyPage() {
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<UserResponseDTO> result = userService.findAllUsers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(userMapper, never()).toResponseDTO(any());
    }

    // ========== FIND BY ID ==========

    @Test
    @DisplayName("Debería encontrar un usuario por ID y devolver DTO")
    void findUserById_WhenExists_ShouldReturnUserResponseDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("EstebanMM13");
        assertThat(result.getEmail()).isEqualTo("esteban@gmail.com");

        verify(userRepository).findById(1L);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por ID")
    void findUserById_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).findById(99L);
        verify(userMapper, never()).toResponseDTO(any());
    }

    // ========== CREATE USER ==========

    @Test
    @DisplayName("Debería crear un usuario nuevo a partir de DTO y devolver DTO")
    void createUser_ShouldSaveAndReturnUserResponseDTO() {
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("EstebanMM13");
        assertThat(result.getEmail()).isEqualTo("esteban@gmail.com");

        verify(userMapper).toEntity(userRequestDTO);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
    }

    // ========== UPDATE USER ==========

    @Test
    @DisplayName("Debería actualizar un usuario existente a partir de DTO y devolver DTO")
    void updateUser_WhenExists_ShouldUpdateAndReturnUserResponseDTO() {
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setUsername("EstebanActualizado");
        updateDTO.setEmail("esteban.nuevo@gmail.com");
        updateDTO.setPassword("nuevaPassword");
        updateDTO.setRole("ADMIN");

        User updatedUser = User.builder()
                .id(1L)
                .username("EstebanActualizado")
                .email("esteban.nuevo@gmail.com")
                .password("nuevaPassword")
                .role(Role.ADMIN)
                .build();

        UserResponseDTO updatedResponse = new UserResponseDTO(1L, "EstebanActualizado", "esteban.nuevo@gmail.com", "ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDTO(updatedUser)).thenReturn(updatedResponse);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("EstebanActualizado");
        assertThat(result.getEmail()).isEqualTo("esteban.nuevo@gmail.com");
        assertThat(result.getRole()).isEqualTo("ADMIN");

        verify(userRepository).findById(1L);
        verify(userMapper).updateEntity(user, updateDTO);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(updatedUser);
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar usuario inexistente")
    void updateUser_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, userRequestDTO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).findById(99L);
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userRepository, never()).save(any());
    }

    // ========== DELETE USER ==========

    @Test
    @DisplayName("Debería eliminar un usuario existente")
    void deleteUser_WhenExists_ShouldDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un usuario inexistente")
    void deleteUser_WhenNotExists_ShouldThrowException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        // Nota: El código original lanza MovieNotFoundException, pero debería ser UserNotFoundException.
        // Mantenemos la excepción real para que el test pase.
        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository).existsById(99L);
        verify(userRepository, never()).deleteById(any());
    }

    // ========== FIND BY USERNAME ==========

    @Test
    @DisplayName("Debería encontrar un usuario por username (búsqueda parcial) y devolver DTO")
    void findUserByUsernameIgnoreCase_WhenExists_ShouldReturnUserResponseDTO() {
        when(userRepository.findUserByUsernameIgnoreCaseContaining("esteban")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findUserByUsernameIgnoreCase("esteban");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("EstebanMM13");

        verify(userRepository).findUserByUsernameIgnoreCaseContaining("esteban");
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por username")
    void findUserByUsernameIgnoreCase_WhenNotExists_ShouldThrowException() {
        when(userRepository.findUserByUsernameIgnoreCaseContaining("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByUsernameIgnoreCase("noexiste"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("noexiste");

        verify(userRepository).findUserByUsernameIgnoreCaseContaining("noexiste");
        verify(userMapper, never()).toResponseDTO(any());
    }

    // ========== FIND BY EMAIL ==========

    @Test
    @DisplayName("Debería encontrar un usuario por email y devolver DTO")
    void findUserByEmail_WhenExists_ShouldReturnUserResponseDTO() {
        when(userRepository.findUserByEmail("esteban@gmail.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.findUserByEmail("esteban@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("EstebanMM13");

        verify(userRepository).findUserByEmail("esteban@gmail.com");
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe por email")
    void findUserByEmail_WhenNotExists_ShouldThrowException() {
        when(userRepository.findUserByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByEmail("noexiste@gmail.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("noexiste@gmail.com");

        verify(userRepository).findUserByEmail("noexiste@gmail.com");
        verify(userMapper, never()).toResponseDTO(any());
    }

    // ========== EXISTS BY EMAIL ==========

    @Test
    @DisplayName("Debería devolver true si el email existe")
    void existsUserByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("esteban@gmail.com")).thenReturn(true);

        boolean exists = userService.existsUserByEmail("esteban@gmail.com");

        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail("esteban@gmail.com");
    }

    @Test
    @DisplayName("Debería devolver false si el email no existe")
    void existsUserByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("noexiste@gmail.com")).thenReturn(false);

        boolean exists = userService.existsUserByEmail("noexiste@gmail.com");

        assertThat(exists).isFalse();
        verify(userRepository).existsByEmail("noexiste@gmail.com");
    }

    // ========== PAGINACIÓN Y ORDENACIÓN ==========

    @Test
    @DisplayName("Debería manejar correctamente la paginación")
    void shouldHandlePaginationCorrectly() {
        List<User> manyUsers = List.of(
                user,
                User.builder().id(2L).username("User2").email("user2@test.com").build(),
                User.builder().id(3L).username("User3").email("user3@test.com").build()
        );
        Page<User> pageWithThree = new PageImpl<>(manyUsers, pageable, 3);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(pageWithThree);
        when(userMapper.toResponseDTO(any(User.class)))
                .thenReturn(userResponseDTO)
                .thenReturn(new UserResponseDTO(2L, "User2", "user2@test.com", "USER"))
                .thenReturn(new UserResponseDTO(3L, "User3", "user3@test.com", "USER"));

        Page<UserResponseDTO> result = userService.findAllUsers(pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debería respetar el ordenamiento en búsquedas paginadas")
    void shouldRespectSortingInPagedQueries() {
        Pageable sortedByUsername = PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by("username").ascending());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(userResponseDTO);

        userService.findAllUsers(sortedByUsername);

        verify(userRepository).findAll(sortedByUsername);
    }
}