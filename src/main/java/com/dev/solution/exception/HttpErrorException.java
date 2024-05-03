package com.dev.solution.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpErrorException extends RuntimeException {
    private final int code;

    public HttpErrorException(int code, String message) {
        super(message);
        this.code = code;
    }
}
