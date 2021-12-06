package com.example.Todo.todo_item.dto;

import javax.validation.constraints.NotBlank;

public record UpdateRequest(@NotBlank String content) {
}
