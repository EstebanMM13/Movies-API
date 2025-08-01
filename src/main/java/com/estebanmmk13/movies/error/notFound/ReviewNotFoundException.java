package com.estebanmmk13.movies.error.notFound;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
