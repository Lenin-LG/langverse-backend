package com.microservice.music.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedToAlbumException extends RuntimeException {
    public AccessDeniedToAlbumException(String message) {
        super(message);
    }
}
