package com.estebanmmk13.movies.services;

import com.estebanmmk13.movies.dtoModels.GenreResponseDTO;
import com.estebanmmk13.movies.error.notFound.GenreNotFoundException;
import com.estebanmmk13.movies.mapper.GenreMapper;
import com.estebanmmk13.movies.models.Genre;
import com.estebanmmk13.movies.repositories.GenreRepository;
import com.estebanmmk13.movies.services.genre.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    @MockitoBean
    private GenreMapper genreMapper;

    private Genre genre;
    private GenreResponseDTO genreResponseDTO;
    private Pageable pageable;
    private Page<Genre> genrePage;

    @BeforeEach
    void setUp() {
        genre = Genre.builder()
                .id(1L)
                .name("Acción")
                .build();

        genreResponseDTO = new GenreResponseDTO(1L, "Acción");

        pageable = PageRequest.of(0, 10);
        genrePage = new PageImpl<>(List.of(genre), pageable, 1);
    }

    // ========== TESTS DE LECTURA (con DTOs) ==========

    @Test
    @DisplayName("Debería devolver todos los géneros paginados como DTOs")
    void findAllGenres_ShouldReturnPageOfGenreResponseDTO() {
        // Given
        when(genreRepository.findAll(any(Pageable.class))).thenReturn(genrePage);
        when(genreMapper.toResponseDTO(any(Genre.class))).thenReturn(genreResponseDTO);

        // When
        Page<GenreResponseDTO> result = genreService.findAllGenres(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Acción");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(genreRepository).findAll(pageable);
        verify(genreMapper, times(1)).toResponseDTO(genre);
    }

    @Test
    @DisplayName("Debería devolver un género como DTO cuando existe por ID")
    void findGenreById_WhenExists_ShouldReturnGenreResponseDTO() {
        // Given
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreMapper.toResponseDTO(genre)).thenReturn(genreResponseDTO);

        // When
        GenreResponseDTO result = genreService.findGenreById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Acción");
        verify(genreRepository).findById(1L);
        verify(genreMapper).toResponseDTO(genre);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el género no existe por ID")
    void findGenreById_WhenNotExists_ShouldThrowException() {
        // Given
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> genreService.findGenreById(99L))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("99");

        verify(genreRepository).findById(99L);
        verify(genreMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería encontrar géneros por nombre (búsqueda parcial) y devolver DTOs")
    void findGenreByName_ShouldReturnPageOfGenreResponseDTO() {
        // Given
        String searchTerm = "acc";
        when(genreRepository.findByNameContainingIgnoreCase(eq(searchTerm), any(Pageable.class)))
                .thenReturn(genrePage);
        when(genreMapper.toResponseDTO(any(Genre.class))).thenReturn(genreResponseDTO);

        // When
        Page<GenreResponseDTO> result = genreService.findGenreByName(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).containsIgnoringCase(searchTerm);

        verify(genreRepository).findByNameContainingIgnoreCase(searchTerm, pageable);
        verify(genreMapper, times(1)).toResponseDTO(genre);
    }

    @Test
    @DisplayName("Debería lanzar excepción al buscar con nombre vacío")
    void findGenreByName_WithEmptyName_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> genreService.findGenreByName("", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");

        assertThatThrownBy(() -> genreService.findGenreByName("   ", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");

        verify(genreRepository, never()).findByNameContainingIgnoreCase(any(), any());
        verify(genreMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería devolver página vacía cuando no encuentra géneros por nombre")
    void findGenreByName_WhenNoMatches_ShouldReturnEmptyPage() {
        // Given
        String searchTerm = "xyz";
        Page<Genre> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(genreRepository.findByNameContainingIgnoreCase(eq(searchTerm), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        Page<GenreResponseDTO> result = genreService.findGenreByName(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(genreMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería encontrar género por nombre exacto (ignore case) y devolver DTO")
    void findGenreByExactName_WhenExists_ShouldReturnGenreResponseDTO() {
        // Given
        when(genreRepository.findByNameIgnoreCase("acción")).thenReturn(Optional.of(genre));
        when(genreMapper.toResponseDTO(genre)).thenReturn(genreResponseDTO);

        // When
        GenreResponseDTO result = genreService.findGenreByExactName("acción");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Acción");

        verify(genreRepository).findByNameIgnoreCase("acción");
        verify(genreMapper).toResponseDTO(genre);
    }

    @Test
    @DisplayName("Debería lanzar excepción si no encuentra género por nombre exacto")
    void findGenreByExactName_WhenNotExists_ShouldThrowException() {
        // Given
        when(genreRepository.findByNameIgnoreCase("drama")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> genreService.findGenreByExactName("drama"))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("drama");

        verify(genreRepository).findByNameIgnoreCase("drama");
        verify(genreMapper, never()).toResponseDTO(any());
    }

    // ========== TESTS DE ESCRITURA (sin cambios, devuelven entidad) ==========

    @Test
    @DisplayName("Debería crear un género correctamente")
    void createGenre_ShouldSaveAndReturnGenre() {
        // Given
        Genre newGenre = Genre.builder().name("Comedia").build();
        when(genreRepository.existsByNameIgnoreCase("Comedia")).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenReturn(newGenre);

        // When
        Genre result = genreService.createGenre(newGenre);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Comedia");

        verify(genreRepository).existsByNameIgnoreCase("Comedia");
        verify(genreRepository).save(newGenre);
        verify(genreMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al crear un género duplicado")
    void createGenre_WhenDuplicate_ShouldThrowException() {
        // Given
        Genre duplicateGenre = Genre.builder().name("Acción").build();
        when(genreRepository.existsByNameIgnoreCase("Acción")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> genreService.createGenre(duplicateGenre))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(genreRepository).existsByNameIgnoreCase("Acción");
        verify(genreRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería actualizar un género existente")
    void updateGenre_WhenExists_ShouldUpdateAndReturn() {
        // Given
        Genre updatedDetails = Genre.builder()
                .name("Acción Extrema")
                .build();

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.existsByNameIgnoreCase("Acción Extrema")).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Genre result = genreService.updateGenre(1L, updatedDetails);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Acción Extrema");

        verify(genreRepository).findById(1L);
        verify(genreRepository).existsByNameIgnoreCase("Acción Extrema");
        verify(genreRepository).save(argThat(savedGenre ->
                savedGenre.getName().equals("Acción Extrema")
        ));
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar un género inexistente")
    void updateGenre_WhenNotExists_ShouldThrowException() {
        // Given
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> genreService.updateGenre(99L, genre))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("99");

        verify(genreRepository).findById(99L);
        verify(genreRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción al actualizar a un nombre que ya existe")
    void updateGenre_WhenNameAlreadyExists_ShouldThrowException() {
        // Given
        Genre existingGenre = Genre.builder().id(2L).name("Drama").build();
        Genre updatedDetails = Genre.builder().name("Drama").build();

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.existsByNameIgnoreCase("Drama")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> genreService.updateGenre(1L, updatedDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(genreRepository).findById(1L);
        verify(genreRepository).existsByNameIgnoreCase("Drama");
        verify(genreRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar un género existente")
    void deleteGenre_WhenExists_ShouldDelete() {
        // Given
        when(genreRepository.existsById(1L)).thenReturn(true);

        // When
        genreService.deleteGenre(1L);

        // Then
        verify(genreRepository).existsById(1L);
        verify(genreRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un género inexistente")
    void deleteGenre_WhenNotExists_ShouldThrowException() {
        // Given
        when(genreRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> genreService.deleteGenre(99L))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessageContaining("99");

        verify(genreRepository).existsById(99L);
        verify(genreRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debería manejar correctamente la paginación")
    void shouldHandlePaginationCorrectly() {
        // Given
        List<Genre> manyGenres = List.of(
                genre,
                Genre.builder().id(2L).name("Comedia").build(),
                Genre.builder().id(3L).name("Drama").build()
        );
        Page<Genre> pageWithThree = new PageImpl<>(manyGenres, pageable, 3);

        when(genreRepository.findAll(any(Pageable.class))).thenReturn(pageWithThree);
        when(genreMapper.toResponseDTO(any(Genre.class)))
                .thenReturn(genreResponseDTO)
                .thenReturn(new GenreResponseDTO(2L, "Comedia"))
                .thenReturn(new GenreResponseDTO(3L, "Drama"));

        // When
        Page<GenreResponseDTO> result = genreService.findAllGenres(pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
    }
}