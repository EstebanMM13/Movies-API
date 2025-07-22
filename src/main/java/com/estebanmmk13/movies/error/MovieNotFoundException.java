package com.estebanmmk13.movies.error;

public class MovieNotFoundException extends Exception{

    public MovieNotFoundException(String message) {
        super(message);
    }
}
