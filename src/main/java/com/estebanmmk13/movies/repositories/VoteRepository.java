package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Movie;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByUserAndMovie(User user, Movie movie);
}

