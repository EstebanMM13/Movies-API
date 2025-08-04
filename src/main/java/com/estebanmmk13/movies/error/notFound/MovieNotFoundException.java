package com.estebanmmk13.movies.error.notFound;

public class MovieNotFoundException extends RuntimeException{

    public static final String NOT_FOUND_BY_ID = "Movie with ID %d not found";
    public static final String NOT_FOUND_BY_TITLE = "Movie with title '%s' not found";

    public static final String NOT_FOUND_BY_GENRE = "Movie with genre '%s' not found";

    public MovieNotFoundException(String message) {
        super(message);
    }
}
