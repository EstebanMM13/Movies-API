package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByUsernameIgnoreCaseContaining(String username);
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);

}
