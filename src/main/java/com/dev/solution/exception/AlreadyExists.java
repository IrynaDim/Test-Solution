package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExists extends HttpErrorException {
    public AlreadyExists(String massage) {
        super(HttpStatus.CONFLICT.value(), massage);
    }
}
