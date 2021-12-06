package com.example.Todo.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    private static final String MESSAGE = "Invalid credentials";
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public InvalidCredentialsException() {
        super(MESSAGE);
        this.status = STATUS;
    }

    public InvalidCredentialsException(String message) {
        super(message);
        this.status = STATUS;
    }

    public InvalidCredentialsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidCredentialsException(String message, HttpStatus status, Throwable throwable) {
        super(message);
        this.status = status;
        this.throwable = throwable;
    }
}