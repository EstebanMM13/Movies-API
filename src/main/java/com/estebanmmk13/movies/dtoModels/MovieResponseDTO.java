package com.estebanmmk13.movies.dtoModels;

import lombok.Data;
import java.util.List;

@Data
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int movieYear;
    private int votes;
    private double rating;
    private String imageUrl;
    private List<GenreResponseDTO> genres;   // lista de géneros con id y nombre

    // Constructor con todos los campos (para facilitar la creación)
    public MovieResponseDTO(Long id, String title, String description, int movieYear,
                            int votes, double rating, String imageUrl, List<GenreResponseDTO> genres) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.movieYear = movieYear;
        this.votes = votes;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.genres = genres;
    }
}