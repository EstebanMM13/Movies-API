package com.estebanmmk13.movies.services.user;

import com.estebanmmk13.movies.dtoModels.request.UserRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.UserResponseDTO;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    void deleteUser(Long id);

    UserResponseDTO findUserByUsernameIgnoreCase(String username);

    UserResponseDTO findUserByEmail(String email);

    boolean existsUserByEmail(String email);

    long countUsers();

    long countUsersByRole(Role role);

    User findUserEntityByUsername(String username);

}