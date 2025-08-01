package com.estebanmmk13.movies.error.notFound;

public class GenreNotFoundException extends RuntimeException{

    public GenreNotFoundException(String message) {
        super(message);
    }
}
