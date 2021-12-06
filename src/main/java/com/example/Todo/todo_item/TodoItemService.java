package com.example.Todo.todo_item;

import com.example.Todo.exception.EntityNotFoundException;
import com.example.Todo.todo_item.dto.CreateRequest;
import com.example.Todo.todo_item.dto.UpdateRequest;
import com.example.Todo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodoItemService {
    private final TodoItemRepository todoItemRepository;
    private final Validator validator;

    // TodoItem list endpoint
    public Page<TodoItem> getTodos(User user, Pageable pageable) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(pageable);

        return todoItemRepository.findAllByUserId(user.getId(), pageable);
    }

    // TodoItem create endpoint
    @Transactional
    public TodoItem createTodo(User user, CreateRequest createRequest) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(createRequest.content());

        Set<ConstraintViolation<CreateRequest>> violations = validator.validate(createRequest);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        TodoItem todoItem = new TodoItem(createRequest.content(), user.getId());

        return todoItemRepository.save(todoItem);
    }

    // TodoItem get endpoint
    public TodoItem getTodo(User user, Long id) throws EntityNotFoundException {
        Objects.requireNonNull(user);
        Objects.requireNonNull(id);

        return getTodoItem(user, id);
    }

    // TodoItem delete endpoint
    public void deleteTodo(User user, Long id) throws EntityNotFoundException {
        Objects.requireNonNull(user);
        Objects.requireNonNull(id);

        boolean tdExist = todoItemRepository.existsByUserIdAndId(user.getId(), id);

        if(tdExist) {
            todoItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Todo item not found");
        }
    }

    // TodoItem update endpoint
    @Transactional
    public TodoItem updateTodo(
            User user,
            Long todoItemId,
            UpdateRequest updateRequest
    ) throws EntityNotFoundException {
        Objects.requireNonNull(user);
        Objects.requireNonNull(todoItemId);
        Objects.requireNonNull(updateRequest);

        Set<ConstraintViolation<UpdateRequest>> violations = validator.validate(updateRequest);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        TodoItem todoItem = getTodoItem(user, todoItemId);
        todoItem.setContent(updateRequest.content());

        return todoItem;
    }

    private TodoItem getTodoItem(User user, Long id) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(id);

        return todoItemRepository.findByUserIdAndId(user.getId(), id).orElseThrow(
                () -> new EntityNotFoundException("Todo item not found")
        );
    }
}
