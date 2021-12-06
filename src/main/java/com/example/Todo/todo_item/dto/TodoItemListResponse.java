package com.example.Todo.todo_item.dto;

import com.example.Todo.todo_item.TodoItem;
import com.example.Todo.shared.dto.PaginationMeta;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class TodoItemListResponse {
    List<TodoItemResponse> todoItems;
    PaginationMeta paginationMeta;

    public TodoItemListResponse(List<TodoItem> todoItems, PaginationMeta paginationMeta) {
        this.todoItems = mapTodoItems(todoItems);
        this.paginationMeta = paginationMeta;
    }

    private List<TodoItemResponse> mapTodoItems(List<TodoItem> todoItems) {
        return todoItems.stream().map(TodoItemResponse::new).collect(Collectors.toList());
    }
}
