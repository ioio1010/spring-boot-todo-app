package com.example.Todo.auth;

import com.example.Todo.auth.dto.SignInRequest;
import com.example.Todo.auth.jwt.JWTAuthData;
import com.example.Todo.auth.dto.JWTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("sign_in")
    public ResponseEntity<JWTResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        JWTAuthData jwtAuthData = authService.signIn(signInRequest, request);

        JWTResponse response = new JWTResponse(jwtAuthData.accessToken(), jwtAuthData.refreshToken());

        return ResponseEntity.ok(response);
    }

    @GetMapping("refresh_token")
    public ResponseEntity<JWTResponse> refreshToken(HttpServletRequest request) {
        JWTAuthData jwtAuthData = authService.refreshToken(request);

        JWTResponse response = new JWTResponse(jwtAuthData.accessToken(), jwtAuthData.refreshToken());

        return ResponseEntity.ok(response);
    }
}
