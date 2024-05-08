package com.dev.solution.exception;

import org.springframework.http.HttpStatus;

public class NotValidFieldsException extends HttpErrorException {
    public NotValidFieldsException(Object message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

    public NotValidFieldsException() {
        super(HttpStatus.BAD_REQUEST.value());
    }
}