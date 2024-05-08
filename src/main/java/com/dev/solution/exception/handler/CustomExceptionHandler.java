package com.dev.solution.exception.handler;

import com.dev.solution.exception.HttpErrorException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles errors that are inherited from the class HttpErrorException.
     * Send message, Time Stamp, url and code of the error.
     **/
    @ExceptionHandler(value = HttpErrorException.class)
    public ResponseEntity<Object> handleHttpException(HttpErrorException exception, WebRequest request) {
        return buildExceptionBody(exception.getMessages(), HttpStatus.valueOf(exception.getCode()), request);
    }

    /**
     * Handles method argument validation errors, typically triggered by {@link MethodArgumentNotValidException}.
     * Returns a ResponseEntity with the appropriate error message, status code, and additional information such as timestamp and URL.
     **/
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return buildExceptionBody(ex.getBindingResult().getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList()),
                HttpStatus.valueOf(status.value()),
                request);
    }

    private ResponseEntity<Object> buildExceptionBody(Object message, HttpStatus httpStatus, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(message);
        exceptionResponse.setStatus(httpStatus.value());
        exceptionResponse.setTimeStamp(LocalDateTime.now().toString());
        exceptionResponse.setUrl(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(httpStatus)
                .body(exceptionResponse);
    }
}
