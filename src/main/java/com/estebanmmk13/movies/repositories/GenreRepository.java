package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Long> {

    Optional<Page<Genre>> findGenreByNameContaining(String name, Pageable pageable);

}
