package com.example.Todo.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException  {
    private static final String MESSAGE = "Bad request";
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public BadRequestException() {
        super(MESSAGE);
        this.status = STATUS;
    }

    public BadRequestException(String message) {
        super(message);
        this.status = STATUS;
    }

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BadRequestException(String message, HttpStatus status, Throwable throwable) {
        super(message);
        this.status = status;
        this.throwable = throwable;
    }
}