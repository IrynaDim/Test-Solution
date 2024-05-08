package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends HttpErrorException {
    public AlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }

    public AlreadyExistsException() {
        super(HttpStatus.CONFLICT.value());
    }
}
