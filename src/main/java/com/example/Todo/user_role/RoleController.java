package com.example.Todo.user_role;

import com.example.Todo.user_role.dto.RoleListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/user_roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<RoleListResponse> getRoles() {
        List<Role> roles = roleService.getRoles();

        RoleListResponse response = new RoleListResponse(roles);

        return ResponseEntity.ok().body(response);
    }
}
