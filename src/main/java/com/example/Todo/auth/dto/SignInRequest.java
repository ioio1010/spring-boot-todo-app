package com.example.Todo.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}