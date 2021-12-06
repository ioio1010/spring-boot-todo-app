package com.example.Todo.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApiException {
    private static final String MESSAGE = "Resource not found";
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public EntityNotFoundException() {
        super(MESSAGE);
        this.status = STATUS;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.status = STATUS;
    }

    public EntityNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public EntityNotFoundException(String message, HttpStatus status, Throwable throwable) {
        super(message);
        this.status = status;
        this.throwable = throwable;
    }
}
