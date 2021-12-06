package com.example.Todo.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private List<Error> errors;
    private String trace;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Data
    @AllArgsConstructor
    private static class Error {
        private String field;
        private String message;
    }

    public void addValidationError(String field, String message){
        if(Objects.isNull(errors)){
            errors = new ArrayList<>();
        }
        errors.add(new Error(field, message));
    }
}
