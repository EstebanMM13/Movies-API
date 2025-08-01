package com.estebanmmk13.movies.services.user;

import com.estebanmmk13.movies.error.notFound.MovieNotFoundException;
import com.estebanmmk13.movies.error.notFound.UserNotFoundException;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {return userRepository.findAll();}

    @Override
    public User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public User createUser(User user) {return userRepository.save(user);}

    @Override
    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new MovieNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User findUserByUsernameIgnoreCase(String username) {
        return userRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

