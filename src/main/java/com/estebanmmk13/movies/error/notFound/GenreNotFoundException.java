package com.estebanmmk13.movies.error.notFound;

public class GenreNotFoundException extends RuntimeException{

    public static final String NOT_FOUND_BY_ID = "Genre with ID %d not found";
    public static final String NOT_FOUND_BY_NAME = "Genre with name '%s' not found";

    public GenreNotFoundException(String message) {
        super(message);
    }
}
