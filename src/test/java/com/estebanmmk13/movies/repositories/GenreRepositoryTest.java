package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        Genre genre1 = Genre.builder().name("Terror").build();
        Genre genre2 = Genre.builder().name("Comedia").build();

        testEntityManager.persist(genre1);
        testEntityManager.persist(genre2);
    }

    @Test
    void shouldFindGenreById() {
        Genre genre = Genre.builder().name("Drama").build();
        Genre saved = testEntityManager.persist(genre);

        Optional<Genre> found = genreRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Drama", found.get().getName());
    }

    @Test
    void findGenreByNameIgnoreCaseFound() {
        Optional<Genre> found = genreRepository.findGenreByNameIgnoreCase("terrOR");

        assertTrue(found.isPresent());
        assertEquals("Terror", found.get().getName());
    }

    @Test
    void findGenreByNameIgnoreCaseNotFound() {
        Optional<Genre> found = genreRepository.findGenreByNameIgnoreCase("Accion");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldSaveGenreSuccessfully() {
        Genre genre = Genre.builder().name("Aventura").build();
        Genre saved = genreRepository.save(genre);

        assertNotNull(saved.getId());
        assertEquals("Aventura", saved.getName());
    }

    @Test
    void shouldDeleteGenreSuccessfully() {
        Genre genre = Genre.builder().name("Fantasía").build();
        Genre saved = testEntityManager.persist(genre);

        genreRepository.deleteById(saved.getId());

        Optional<Genre> deleted = genreRepository.findById(saved.getId());
        assertTrue(deleted.isEmpty());
    }
}
