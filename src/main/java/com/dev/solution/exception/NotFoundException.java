package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpErrorException {
    public NotFoundException(String massage) {
        super(HttpStatus.NOT_FOUND.value(), massage);
    }
}
