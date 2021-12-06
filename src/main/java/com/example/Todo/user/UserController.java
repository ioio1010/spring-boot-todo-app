package com.example.Todo.user;

import com.example.Todo.exception.EntityNotFoundException;
import com.example.Todo.user.dto.*;
import com.example.Todo.shared.dto.PaginationMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("sign_up")
    public ResponseEntity<UserResponse> createUser(@RequestBody SignUpRequest signUpRequest) {
        User user = userService.createUser(signUpRequest);

        UserResponse response = new UserResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<UserListResponse> getUsers(
            @RequestBody(required = false) UserFilterRequest userFilterRequest,
            Pageable pageable
    ) {
        Page<User> usersPage = userService.getUsers(userFilterRequest, pageable);

        UserListResponse response = new UserListResponse(
                usersPage.getContent(),
                new PaginationMeta(
                        usersPage.getPageable().getPageNumber(),
                        usersPage.getTotalPages(),
                        usersPage.getTotalElements(),
                        usersPage.isLast()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("id") Long id,
            @RequestParam("avatar") MultipartFile avatarFile
    ) throws IOException {
        User user = userService.updateUser(findCurrentUser(), avatarFile);

        UserResponse response = new UserResponse(user);

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


