package com.example.Todo.user_role.dto;

import com.example.Todo.user_role.Role;
import lombok.Value;

import java.util.Objects;

@Value
public class RoleResponse {
    Long id;
    String roleType;

    public RoleResponse(Role role) {
        Objects.requireNonNull(role);

        this.id = role.getId();
        this.roleType = role.getRoleType().toString();
    }
}
