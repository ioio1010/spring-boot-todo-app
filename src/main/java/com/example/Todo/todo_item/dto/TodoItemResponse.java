package com.example.Todo.todo_item.dto;

import com.example.Todo.todo_item.TodoItem;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TodoItemResponse {
    Long id;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public TodoItemResponse(TodoItem todoItem) {
        this.id = todoItem.getId();
        this.content = todoItem.getContent();
        this.createdAt = todoItem.getCreatedAt();
        this.updatedAt = todoItem.getUpdatedAt();
    }
}
