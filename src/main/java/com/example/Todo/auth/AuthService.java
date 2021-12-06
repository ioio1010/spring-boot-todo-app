package com.example.Todo.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Todo.auth.dto.SignInRequest;
import com.example.Todo.auth.jwt.JWTAuthData;
import com.example.Todo.auth.jwt.JWTService;
import com.example.Todo.exception.EntityNotFoundException;
import com.example.Todo.exception.InvalidAuthTokenException;
import com.example.Todo.exception.InvalidCredentialsException;
import com.example.Todo.user.User;
import com.example.Todo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JWTService jwtService;
    private final Validator validator;

    // Auth signIn endpoint
    public JWTAuthData signIn(SignInRequest signInRequest, HttpServletRequest request) {
        Objects.requireNonNull(signInRequest);
        Objects.requireNonNull(request);

        Set<ConstraintViolation<SignInRequest>> violations = validator.validate(signInRequest);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        User user = getUser(signInRequest.email(), signInRequest.password());

        return new JWTAuthData(
                jwtService.createAccessToken(user, request.getRequestURL().toString()),
                jwtService.createRefreshToken(user, request.getRequestURL().toString())
        );
    }

    private User getUser(String email, String password) {
        return userService.getUserBy(email, password).orElseThrow(
                () -> new InvalidCredentialsException("Invalid credentials")
        );
    }

    // Auth refreshToken endpoint
    public JWTAuthData refreshToken(HttpServletRequest request) throws JWTVerificationException, EntityNotFoundException {
        Objects.requireNonNull(request);

        String refreshToken = getToken(request);
        DecodedJWT decodedJWT = jwtService.getDecodedToken(refreshToken);
        User user = getUser(decodedJWT.getSubject());

        return new JWTAuthData(
                jwtService.createAccessToken(user, request.getRequestURL().toString()),
                refreshToken
        );
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthTokenException("Invalid authorization header");
        }

        return authHeader.substring("Bearer ".length());
    }

    private User getUser(String email) {
        return userService.getUserBy(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }
}
