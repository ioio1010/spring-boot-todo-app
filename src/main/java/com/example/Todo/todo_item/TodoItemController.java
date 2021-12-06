package com.example.Todo.todo_item;

import com.example.Todo.exception.EntityNotFoundException;
import com.example.Todo.todo_item.dto.CreateRequest;
import com.example.Todo.todo_item.dto.TodoItemListResponse;
import com.example.Todo.todo_item.dto.TodoItemResponse;
import com.example.Todo.todo_item.dto.UpdateRequest;
import com.example.Todo.user.User;
import com.example.Todo.user.UserService;
import com.example.Todo.shared.dto.PaginationMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/todo_items")
@RequiredArgsConstructor
public class TodoItemController {
    private final TodoItemService todoItemService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<TodoItemListResponse> getTodos(Pageable pageable) {
        Page<TodoItem> todoItemsPage = todoItemService.getTodos(findCurrentUser(), pageable);

        TodoItemListResponse response = new TodoItemListResponse(
                todoItemsPage.getContent(),
                new PaginationMeta(
                        todoItemsPage.getPageable().getPageNumber(),
                        todoItemsPage.getTotalPages(),
                        todoItemsPage.getTotalElements(),
                        todoItemsPage.isLast()
                )
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<TodoItemResponse> getTodo(@PathVariable("id") Long id) {
        TodoItem todoItem = todoItemService.getTodo(findCurrentUser(), id);

        TodoItemResponse response = new TodoItemResponse(todoItem);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TodoItemResponse> createTodo(@RequestBody CreateRequest createRequest) {
        TodoItem todoItem = todoItemService.createTodo(findCurrentUser(), createRequest);

        TodoItemResponse response = new TodoItemResponse(todoItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") Long id) {
       todoItemService.deleteTodo(findCurrentUser(), id);

       return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<TodoItemResponse> updateTodo(
            @PathVariable("id") Long id,
            @RequestBody UpdateRequest updateRequest
    ) {
        TodoItem todoItem = todoItemService.updateTodo(findCurrentUser(), id, updateRequest);

        TodoItemResponse response = new TodoItemResponse(todoItem);

        return ResponseEntity.ok(response);
    }

    private User findCurrentUser() {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        return userService.getUserBy(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }
}
