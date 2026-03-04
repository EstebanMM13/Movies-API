package com.estebanmmk13.movies.error;

import com.estebanmmk13.movies.error.dto.ResponseError;
import com.estebanmmk13.movies.error.notFound.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            MovieNotFoundException.class,
            UserNotFoundException.class,
            GenreNotFoundException.class,
            ReviewNotFoundException.class,
            VoteNotFoundException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<ResponseError> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest request) {
        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(DuplicateVoteException.class)
    public ResponseEntity<ResponseError> handleDuplicateVoteException(DuplicateVoteException ex, HttpServletRequest request) {
        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseError);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ResponseError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseError);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ResponseError> handleInvalidCredentials(Exception ex, HttpServletRequest request) {
        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "User or passwoed incorrect",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ResponseError responseError = new ResponseError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Error de validación en los campos enviados",
                path
        );

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", responseError.getTimestamp());
        responseBody.put("status", responseError.getStatus());
        responseBody.put("error", responseError.getError());
        responseBody.put("message", responseError.getMessage());
        responseBody.put("path", responseError.getPath());
        responseBody.put("fieldErrors", fieldErrors);  // Nombre más claro

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}