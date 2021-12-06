package com.example.Todo.user.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserFilterRequest(
        String email,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt
) {
}
