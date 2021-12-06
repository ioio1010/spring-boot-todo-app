package com.example.Todo.todo_item;

import com.example.Todo.exception.EntityNotFoundException;
import com.example.Todo.user.User;
import com.example.Todo.user_role.Role;
import com.example.Todo.user_role.RoleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoItemServiceTest {
    @Mock
    private TodoItemRepository todoItemRepository;

    @InjectMocks
    private TodoItemService todoItemService;

    private User getUserWithEmailPasswordUserRole() {
        return new User(
                "test@example.com",
                "password",
                List.of(new Role(RoleType.USER))
        );
    }

    @Nested
    class GetTodo {
        @Test
        void givenExistedTodoItemWhenGetTodoThenReturnTodoItem() {
            User givenUser = getUserWithEmailPasswordUserRole();

            TodoItem expectedTodoItem = new TodoItem((long) 1, "Test", givenUser.getId());

            when(todoItemRepository.findByUserIdAndId(expectedTodoItem.getUserId(), expectedTodoItem.getId()))
                    .thenReturn(Optional.of(expectedTodoItem));

            TodoItem actualTodoItem = todoItemService.getTodo(givenUser, expectedTodoItem.getId());

            assertThat(actualTodoItem).isNotNull();
            assertThat(actualTodoItem).isEqualTo(expectedTodoItem);

            verify(todoItemRepository).findByUserIdAndId(expectedTodoItem.getUserId(), expectedTodoItem.getId());
        }

        @Test
        void givenNotExistedTodoItemWhenGetTodoThenReturnEntityNotFoundException() {
            User givenUser = getUserWithEmailPasswordUserRole();
            Long givenNotExistedId = 1L;

            when(todoItemRepository.findByUserIdAndId(givenUser.getId(), givenNotExistedId))
                    .thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(
                            () -> todoItemService.getTodo(givenUser, givenNotExistedId)
                    ).withMessage("Todo item not found");

            verify(todoItemRepository).findByUserIdAndId(givenUser.getId(), givenNotExistedId);
        }
    }

    @Nested
    class DeleteTodo {
        @Test
        void givenExistedTodoItemWhenDeleteTodoThenReturnTodoItem() {
            User givenUser = getUserWithEmailPasswordUserRole();

            TodoItem expectedTodoItem = new TodoItem((long) 1, "Test", givenUser.getId());

            when(todoItemRepository.existsByUserIdAndId(expectedTodoItem.getUserId(), expectedTodoItem.getId()))
                    .thenReturn(true);
            doNothing().when(todoItemRepository).deleteById(expectedTodoItem.getId());

            todoItemService.deleteTodo(givenUser, expectedTodoItem.getId());

            verify(todoItemRepository).existsByUserIdAndId(expectedTodoItem.getUserId(), expectedTodoItem.getId());
            verify(todoItemRepository).deleteById(expectedTodoItem.getId());
        }

        @Test
        void givenNotExistedTodoItemWhenDeleteTodoThenReturnEntityNotFoundException() {
            User givenUser = getUserWithEmailPasswordUserRole();
            Long givenNotExistedId = 1L;

            when(todoItemRepository.existsByUserIdAndId(givenUser.getId(), givenNotExistedId))
                    .thenReturn(false);

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(
                            () -> todoItemService.deleteTodo(givenUser, givenNotExistedId)
                    ).withMessage("Todo item not found");

            verify(todoItemRepository).existsByUserIdAndId(givenUser.getId(), givenNotExistedId);
        }
    }
}