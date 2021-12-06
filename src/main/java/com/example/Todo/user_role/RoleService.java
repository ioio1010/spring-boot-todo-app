package com.example.Todo.user_role;

import com.example.Todo.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    public final static RoleType DEFAULT_ROLE = RoleType.USER;

    private final RoleRepository roleRepository;

    // Roles list endpoint
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getDefaultRole() throws EntityNotFoundException {
        return roleRepository.findByRoleType(DEFAULT_ROLE).orElseThrow(
                () -> new EntityNotFoundException("Role not found")
        );
    }

    public Optional<Role> getRole(RoleType roleType) {
        Objects.requireNonNull(roleType);

        return roleRepository.findByRoleType(roleType);
    }

    public Role createRole(Role role) {
        Objects.requireNonNull(role);

        return roleRepository.save(role);
    }
}
