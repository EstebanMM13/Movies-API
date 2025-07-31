package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.User;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void createUser(){
        User user = User.builder()
                .username("EstebanK13")
                .email("estebancito@gmail.com")
                .password("1234")
                .build();
        userRepository.save(user);
    }

    @Test
    public void findByUsername(){

        User user = User.builder()
                .username("EstebanK13")
                .email("estebancito@gmail.com")
                .password("1234")
                .build();
        userRepository.save(user);

        Optional<User> user2 = userRepository.findByUsername("EstebanK14");
        System.out.println(user2);
    }

    @Test
    public void findAllUsers(){

        User user = User.builder()
                .id(1L)
                .username("EstebanK13")
                .email("estebancito@gmail.com")
                .password("1234")
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .username("MarioCrack")
                .email("mariocrack@gmail.com")
                .password("1234")
                .build();
        userRepository.save(user2);

        List<User> usersList = userRepository.findAll();
        System.out.println(usersList);
    }


}