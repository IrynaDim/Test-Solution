package com.dev.solution.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpErrorException extends RuntimeException {
    private int code;
    private Object messages;

    public HttpErrorException(int code, Object messages) {
        this.code = code;
        this.messages = messages;
    }

    public HttpErrorException(int code) {
        this.code = code;
    }
}
