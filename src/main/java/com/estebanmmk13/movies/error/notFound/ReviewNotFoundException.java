package com.estebanmmk13.movies.error.notFound;

public class ReviewNotFoundException extends RuntimeException {

    public static final String NOT_FOUND_BY_ID = "Review with ID %d not found";
    public static final String NOT_FOUND_BY_MOVIE = "Review with movie ID %d not found";
    public static final String NOT_FOUND_BY_USER  = "Review with user ID %d not found";

    public static final String NOT_ACCES  = "You cannot delete this review";

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
