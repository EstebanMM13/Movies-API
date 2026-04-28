package com.estebanmmk13.movies.dtoModels;

import lombok.Data;

@Data
public class GenreDTO {
    private Long id;
    private String name;

    public GenreDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}