package com.estebanmmk13.movies.error;

public class DuplicateVoteException extends RuntimeException {
    public DuplicateVoteException(String message) {
        super(message);
    }
}

