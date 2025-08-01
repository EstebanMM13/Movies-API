package com.estebanmmk13.movies.error.notFound;

public class VoteNotFoundException extends RuntimeException{

    public VoteNotFoundException(String message) {
        super(message);
    }
}
