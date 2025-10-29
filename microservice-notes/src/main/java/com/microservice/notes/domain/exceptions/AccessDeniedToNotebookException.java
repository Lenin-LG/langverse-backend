package com.microservice.notes.domain.exceptions;

public class AccessDeniedToNotebookException extends  RuntimeException{
    public AccessDeniedToNotebookException(String message) {
        super(message);
    }
}
