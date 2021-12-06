package com.example.Todo.shared.dto;

public record PaginationMeta(
        int pageNumber,
        int totalPages,
        long totalElements,
        boolean isLast
) {
}