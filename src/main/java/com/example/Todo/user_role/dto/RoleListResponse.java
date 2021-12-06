package com.example.Todo.user_role.dto;

import com.example.Todo.user_role.Role;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Value
public class RoleListResponse {
    List<RoleResponse> roles;

    public RoleListResponse(List<Role> roles) {
        this.roles = mapRoles(roles);
    }

    private List<RoleResponse> mapRoles(List<Role> roles) {
        Objects.requireNonNull(roles);

        return roles.stream()
                .map(RoleResponse::new)
                .collect(Collectors.toList());
    }
}
