package com.example.Todo.exception;

import com.example.Todo.exception.dto.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.platform.commons.util.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({
            EntityNotFoundException.class,
            BadRequestException.class,
            InvalidAuthTokenException.class,
            InvalidCredentialsException.class
    })
    protected ResponseEntity<Object> handleApiException(ApiException ex, WebRequest request) {

        ErrorResponse er = new ErrorResponse(
                ex.getStatus().value(),
                ex.getMessage(),
                null,
                ExceptionUtils.readStackTrace(ex)
        );

        return createResponseEntity(ex, er, new HttpHeaders(), ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        return createResponseEntity(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException ex,
                                                                 WebRequest request) {
        return createResponseEntity(ex, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                  WebRequest request) {
        return createResponseEntity(ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        ex.getConstraintViolations().forEach(violation -> er.addValidationError(
                violation.getPropertyPath().toString(),
                violation.getMessage()
        ));

        return createResponseEntity(ex, er, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                           @NotNull HttpHeaders headers,
                                                                           @NotNull HttpStatus status,
                                                                           @NotNull WebRequest request) {

        ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            er.addValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        return createResponseEntity(ex, er, headers, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleExceptionInternal(@NotNull Exception ex,
                                                                      @Nullable Object body,
                                                                      @NotNull HttpHeaders headers,
                                                                      @NotNull HttpStatus status,
                                                                      @NotNull WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }

        return createResponseEntity(ex, headers, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        return createResponseEntity(ex, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Object> createResponseEntity(Exception ex,
                                                        ErrorResponse body,
                                                        HttpHeaders headers,
                                                        HttpStatus status) {

        body.setTrace(ExceptionUtils.readStackTrace(ex));

        return new ResponseEntity<>(body, headers, status);
    }

    private ResponseEntity<Object> createResponseEntity(Exception ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status) {

        ErrorResponse body = new ErrorResponse(status.value(), ex.getMessage());
        body.setTrace(ExceptionUtils.readStackTrace(ex));

        return new ResponseEntity<>(body, headers, status);
    }

}
