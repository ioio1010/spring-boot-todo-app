package com.example.Todo.user.dto;

import com.example.Todo.user.User;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class UserResponse {
    Long id;
    String email;
    String avatar;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<String> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.roles = mapRoles(user);
    }

    private List<String> mapRoles(User user) {
        return user.getRoles()
                .stream()
                .map(role -> role.getRoleType().toString())
                .collect(Collectors.toList());
    }
}
