package com.estebanmmk13.movies.error.notFound;

public class UserNotFoundException extends RuntimeException {

    public static final String NOT_FOUND_BY_ID = "User with ID %d not found";
    public static final String NOT_FOUND_BY_USERNAME = "User with username: '%s' not found";

    public static final String NOT_FOUND_BY_EMAIL = "User with email: '%s' not found";

    public UserNotFoundException(String message) {
        super(message);
    }
}
