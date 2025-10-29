package com.microservice.movie.infrastructure.adapters.in.rest.exception;

public class NoResultsFoundException extends RuntimeException {
    public NoResultsFoundException(String message) {
        super(message);
    }
}