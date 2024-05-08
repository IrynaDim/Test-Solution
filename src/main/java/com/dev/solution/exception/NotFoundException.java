package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpErrorException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND.value());
    }
}
