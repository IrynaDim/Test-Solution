package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class NotValidFields extends HttpErrorException {
    public NotValidFields(String massage) {
        super(HttpStatus.BAD_REQUEST.value(), massage);
    }
}