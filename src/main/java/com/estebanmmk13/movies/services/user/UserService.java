package com.estebanmmk13.movies.services.user;

import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllUsers();

    User findUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    User findUserByUsernameIgnoreCase(String username);

    User findUserByEmail(String email);

    boolean existsUserByEmail(String email);
}

