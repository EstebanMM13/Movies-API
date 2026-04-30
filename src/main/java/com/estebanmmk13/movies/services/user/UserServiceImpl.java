package com.estebanmmk13.movies.services.user;

import com.estebanmmk13.movies.dtoModels.request.UserRequestDTO;
import com.estebanmmk13.movies.dtoModels.response.UserResponseDTO;
import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.mapper.UserMapper;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.estebanmmk13.movies.error.notFound.UserNotFoundException.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDTO);
    }

    @Override
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        // Encriptar contraseña aquí si no lo hace un listener
        User saved = userRepository.save(user);
        return userMapper.toResponseDTO(saved);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
        userMapper.updateEntity(existingUser, userRequestDTO);
        User updated = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new MovieNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
        userRepository.deleteById(id);
    }


    @Override
    public UserResponseDTO findUserByUsernameIgnoreCase(String username) {
        User user = userRepository.findUserByUsernameIgnoreCaseContaining(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_USERNAME, username)));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format(NOT_FOUND_BY_EMAIL, email)));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}