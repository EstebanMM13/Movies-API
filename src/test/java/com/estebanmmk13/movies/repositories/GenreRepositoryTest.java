package com.estebanmmk13.movies.repositories;

import com.estebanmmk13.movies.models.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should find genre by ID")
    void shouldFindGenreById() {
        // Given
        Genre drama = Genre.builder().name("Drama").build();
        Genre saved = testEntityManager.persist(drama);
        testEntityManager.flush();

        // When
        Optional<Genre> found = genreRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Drama");
    }

    @Test
    @DisplayName("Should find genre by name ignoring case")
    void findByNameIgnoreCase_ShouldReturnGenre() {
        // Given - cada test crea sus propios datos
        Genre terror = Genre.builder().name("Terror").build();
        testEntityManager.persist(terror);
        testEntityManager.flush();

        // When
        Optional<Genre> found = genreRepository.findByNameIgnoreCase("TERROR");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Terror");
    }

    @Test
    @DisplayName("Should return empty when genre not found by name")
    void findByNameIgnoreCase_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Genre> found = genreRepository.findByNameIgnoreCase("Acción");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find genres by name containing (case insensitive)")
    void findByNameContainingIgnoreCase_ShouldReturnMatchingGenres() {
        // Given - Limpiamos y creamos datos SOLO para este test
        genreRepository.deleteAll();
        testEntityManager.clear();
        testEntityManager.flush();

        Genre comedia = Genre.builder().name("Comedia").build();
        Genre comediaRomantica = Genre.builder().name("Comedia Romántica").build();
        Genre terror = Genre.builder().name("Terror").build();

        testEntityManager.persist(comedia);
        testEntityManager.persist(comediaRomantica);
        testEntityManager.persist(terror);
        testEntityManager.flush();

        // When - buscamos todos los que contengan "com"
        Page<Genre> result = genreRepository.findByNameContainingIgnoreCase("com", pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Comedia", "Comedia Romántica");
    }

    @Test
    @DisplayName("Should return empty page when no genres match the search term")
    void findByNameContainingIgnoreCase_ShouldReturnEmptyPage_WhenNoMatches() {
        // When
        Page<Genre> result = genreRepository.findByNameContainingIgnoreCase("xyz", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should check if genre exists by name (case insensitive)")
    void existsByNameIgnoreCase_ShouldReturnCorrectBoolean() {
        // Given - limpiar y crear datos específicos
        genreRepository.deleteAll();
        testEntityManager.clear();
        testEntityManager.flush();

        Genre terror = Genre.builder().name("Terror").build();
        Genre comedia = Genre.builder().name("Comedia").build();

        testEntityManager.persist(terror);
        testEntityManager.persist(comedia);
        testEntityManager.flush();

        // When
        boolean existsTerror = genreRepository.existsByNameIgnoreCase("TERROR");
        boolean existsComedia = genreRepository.existsByNameIgnoreCase("comedia");
        boolean existsAccion = genreRepository.existsByNameIgnoreCase("Acción");

        // Then
        assertThat(existsTerror).isTrue();
        assertThat(existsComedia).isTrue();
        assertThat(existsAccion).isFalse();
    }

    @Test
    @DisplayName("Should save genre successfully")
    void shouldSaveGenreSuccessfully() {
        // Given
        Genre aventura = Genre.builder().name("Aventura").build();

        // When
        Genre saved = genreRepository.save(aventura);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Aventura");

        // Verificar que se guardó en BD
        Optional<Genre> found = genreRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Aventura");
    }

    @Test
    @DisplayName("Should delete genre successfully")
    void shouldDeleteGenreSuccessfully() {
        // Given
        Genre fantasia = Genre.builder().name("Fantasía").build();
        Genre saved = testEntityManager.persist(fantasia);
        testEntityManager.flush();

        // When
        genreRepository.deleteById(saved.getId());

        // Then
        Optional<Genre> deleted = genreRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should update genre successfully")
    void shouldUpdateGenre() {
        // Given
        Genre ciencia = Genre.builder().name("Ciencia Ficción").build();
        Genre saved = testEntityManager.persist(ciencia);
        testEntityManager.flush();

        // When
        saved.setName("Sci-Fi");
        Genre updated = genreRepository.save(saved);

        // Then
        Optional<Genre> found = genreRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Sci-Fi");
    }

    @Test
    @DisplayName("Should return paginated results correctly")
    void shouldReturnPaginatedResults() {
        // Given - limpiar y crear datos
        genreRepository.deleteAll();
        testEntityManager.clear();
        testEntityManager.flush();

        // Crear exactamente 15 géneros nuevos
        for (int i = 0; i < 15; i++) {
            Genre genre = Genre.builder().name("Género " + i).build();
            testEntityManager.persist(genre);
        }
        testEntityManager.flush();

        Pageable firstPage = PageRequest.of(0, 10);
        Pageable secondPage = PageRequest.of(1, 10);

        // When
        Page<Genre> page1 = genreRepository.findAll(firstPage);
        Page<Genre> page2 = genreRepository.findAll(secondPage);

        // Then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page2.getContent()).hasSize(5); // 15 total - 10 = 5
        assertThat(page1.getTotalElements()).isEqualTo(15);
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle pagination with search")
    void shouldReturnPaginatedResultsWithSearch() {
        // Given - cada test crea sus propios datos
        String[] nombres = {
                "Acción", "Aventura", "Animación", "Artes Marciales",
                "Bélica", "Biografía", "Ciencia Ficción", "Crimen",
                "Comedia", "Documental", "Drama", "Fantasía"
        };

        for (String nombre : nombres) {
            testEntityManager.persist(Genre.builder().name(nombre).build());
        }
        testEntityManager.flush();

        Pageable firstPage = PageRequest.of(0, 5);

        // When
        Page<Genre> result = genreRepository.findByNameContainingIgnoreCase("a", firstPage);

        // Then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isGreaterThan(5);
    }
}