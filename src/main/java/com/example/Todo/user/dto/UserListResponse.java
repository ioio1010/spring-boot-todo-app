package com.example.Todo.user.dto;

import com.example.Todo.user.User;
import com.example.Todo.shared.dto.PaginationMeta;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class UserListResponse {
    List<UserResponse> users;
    PaginationMeta paginationMeta;

    public UserListResponse(List<User> users, PaginationMeta paginationMeta) {
        this.users = mapUsers(users);
        this.paginationMeta = paginationMeta;
    }

    private List<UserResponse> mapUsers(List<User> users) {
        return users.stream().map(UserResponse::new).collect(Collectors.toList());
    }
}
