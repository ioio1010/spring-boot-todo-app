package com.example.Todo.user.dto;

import com.example.Todo.validation.FieldsMatch;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@FieldsMatch(firstFieldName = "password", secondFieldName = "passwordConfirmation")
public class SignUpRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
                message = "Password must contains: at least 8 characters and at most 20 characters, " +
                        "one digit, " +
                        "one upper case alphabet, " +
                        "one lower case alphabet, " +
                        "one special character which includes @#$%^&+=, " +
                        "doesnt contain any white space"
        )
        private String password;

        @NotBlank
        private String passwordConfirmation;
}

