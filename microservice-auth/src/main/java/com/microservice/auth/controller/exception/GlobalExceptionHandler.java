package com.microservice.auth.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Manage conflicts (409)
    @ExceptionHandler(WebClientResponseException.Conflict.class)
    public ResponseEntity<String> handleConflict(WebClientResponseException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflict: user data already exists.");
    }

    // Handles other WebClient errors
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientException(WebClientResponseException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body("Error: " + e.getResponseBodyAsString());
    }

    // Handles general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + e.getMessage());
    }
    // 401 Unauthorized
    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public ResponseEntity<String> handleUnauthorized(WebClientResponseException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Incorrect username or password");
    }

    // 403 Forbidden
    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<String> handleForbidden(WebClientResponseException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User account is disabled or does not have access");
    }
}
