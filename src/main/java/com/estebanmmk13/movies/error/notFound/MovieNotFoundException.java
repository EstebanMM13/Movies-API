package com.estebanmmk13.movies.error.notFound;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String message) {
        super(message);
    }
}
