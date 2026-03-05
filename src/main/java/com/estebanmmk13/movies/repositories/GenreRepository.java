package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Long> {

    // Busca géneros por nombre exacto (ignore case)
    Optional<Genre> findByNameIgnoreCase(String name);

    // Busca géneros que contengan el texto (para búsquedas parciales)
    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}