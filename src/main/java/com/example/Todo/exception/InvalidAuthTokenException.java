package com.example.Todo.exception;

import org.springframework.http.HttpStatus;

public class InvalidAuthTokenException extends ApiException {
    private static final String MESSAGE = "Invalid authorization token";
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public InvalidAuthTokenException() {
        super(MESSAGE);
        this.status = STATUS;
    }

    public InvalidAuthTokenException(String message) {
        super(message);
        this.status = STATUS;
    }

    public InvalidAuthTokenException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidAuthTokenException(String message, HttpStatus status, Throwable throwable) {
        super(message);
        this.status = status;
        this.throwable = throwable;
    }
}
