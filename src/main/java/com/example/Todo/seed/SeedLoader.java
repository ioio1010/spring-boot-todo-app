package com.example.Todo.seed;

import com.example.Todo.user.UserService;
import com.example.Todo.user_role.Role;
import com.example.Todo.user_role.RoleService;
import com.example.Todo.user_role.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("development")
public class SeedLoader implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        Role roleUser = roleService.getRole(RoleType.USER).orElseGet(() -> {
                    return roleService.createRole(new Role(RoleType.USER));
        });
        Role roleManager = roleService.getRole(RoleType.MANAGER).orElseGet(() -> {
                    return roleService.createRole(new Role(RoleType.MANAGER));
        });
        Role roleAdmin = roleService.getRole(RoleType.ADMIN).orElseGet(() -> {
            return roleService.createRole(new Role(RoleType.ADMIN));
        });
        Role roleSuperAdmin = roleService.getRole(RoleType.SUPER_ADMIN).orElseGet(() -> {
            return roleService.createRole(new Role(RoleType.SUPER_ADMIN));
        });

        userService.getUserBy("user@example.com").orElseGet(() -> {
            return userService.createUser(
                    "user@example.com",
                    "Password@1",
                    List.of(roleUser));
        });
        userService.getUserBy("manager@example.com").orElseGet(() -> {
            return userService.createUser(
                    "manager@example.com",
                    "Password@1",
                    List.of(roleManager));
        });
        userService.getUserBy("superadmin@example.com").orElseGet(() -> {
            return userService.createUser(
                    "superadmin@example.com",
                    "Password@1",
                    List.of(roleAdmin, roleSuperAdmin));
        });

        log.info("Seed data loaded");
    }
}
