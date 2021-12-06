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
import com.example.Todo.user_role.Role;
import com.example.Todo.user_role.RoleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JWTService jwtService;

    @Mock
    private Validator validator;

    @InjectMocks
    private AuthService authService;

    @Nested
    class SignIn {
        @Test
        void givenValidCredentialsWhenSignThenReturnJWTAuthData() {
            String givenEmail = "test@example.com";
            String givenPassword = "Password@1";
            String givenRequestURL = "https://test.com";
            User givenUser = new User(
                    givenEmail,
                    givenPassword,
                    List.of(new Role(RoleType.USER))
            );
            SignInRequest givenSignInRequest = new SignInRequest(givenEmail, givenPassword);

            String expectedAccessToken = "accessToken";
            String expectedRefreshToken = "refreshToken";

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(mockHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(givenRequestURL));
            when(validator.validate(givenSignInRequest)).thenReturn(Set.of());
            when(userService.getUserBy(givenEmail, givenPassword)).thenReturn(Optional.of(givenUser));
            when(jwtService.createAccessToken(givenUser, givenRequestURL)).thenReturn(expectedAccessToken);
            when(jwtService.createRefreshToken(givenUser, givenRequestURL)).thenReturn(expectedRefreshToken);

            JWTAuthData actualJwtAuthData = authService.signIn(givenSignInRequest, mockHttpServletRequest);

            assertThat(actualJwtAuthData).isNotNull();
            assertThat(actualJwtAuthData.accessToken()).isEqualTo(expectedAccessToken);
            assertThat(actualJwtAuthData.refreshToken()).isEqualTo(expectedRefreshToken);

            verify(validator).validate(givenSignInRequest);
            verify(userService).getUserBy(givenEmail, givenPassword);
            verify(jwtService).createAccessToken(givenUser, givenRequestURL);
            verify(jwtService).createRefreshToken(givenUser, givenRequestURL);
        }

        @Test
        void givenInvalidEmailWhenSignThenReturnInvalidCredentialsException() {
            String givenInvalidEmail = "testINVALID@example.com";
            String givenPassword = "Password@1";
            SignInRequest givenSignInRequest = new SignInRequest(givenInvalidEmail, givenPassword);

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(validator.validate(givenSignInRequest)).thenReturn(Set.of());
            when(userService.getUserBy(givenInvalidEmail, givenPassword)).thenReturn(Optional.empty());

            assertThatExceptionOfType(InvalidCredentialsException.class)
                    .isThrownBy(
                            () -> authService.signIn(givenSignInRequest, mockHttpServletRequest)
                    ).withMessage("Invalid credentials");

            verify(validator).validate(givenSignInRequest);
            verify(userService).getUserBy(givenInvalidEmail, givenPassword);
        }

        @Test
        void givenInvalidPasswordWhenSignThenReturnInvalidCredentialsException() {
            String givenEmail = "test@example.com";
            String givenInvalidPassword = "PasswordINVALID@1";
            SignInRequest givenSignInRequest = new SignInRequest(givenEmail, givenInvalidPassword);

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(validator.validate(givenSignInRequest)).thenReturn(Set.of());
            when(userService.getUserBy(givenEmail, givenInvalidPassword)).thenReturn(Optional.empty());

            assertThatExceptionOfType(InvalidCredentialsException.class)
                    .isThrownBy(
                            () -> authService.signIn(givenSignInRequest, mockHttpServletRequest)
                    ).withMessage("Invalid credentials");

            verify(validator).validate(givenSignInRequest);
            verify(userService).getUserBy(givenEmail, givenInvalidPassword);
        }
    }

    @Nested
    class RefreshToken {
        @Test
        void givenValidAuthorizationTokenWhenRefreshTokenThenReturnJWTAuthData() {
            String givenEmail = "test@example.com";
            String givenPassword = "Password@1";
            String givenRequestURL = "https://test.com";
            User givenUser = new User(
                    givenEmail,
                    givenPassword,
                    List.of(new Role(RoleType.USER))
            );

            String expectedAccessToken = "accessToken";
            String expectedRefreshToken = "refreshToken";

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
            DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);

            when(mockHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(givenRequestURL));
            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + expectedRefreshToken);
            when(jwtService.getDecodedToken(any(String.class))).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(givenEmail);
            when(userService.getUserBy(givenEmail)).thenReturn(Optional.of(givenUser));
            when(jwtService.createAccessToken(givenUser, givenRequestURL)).thenReturn(expectedAccessToken);

            JWTAuthData actualJwtAuthData = authService.refreshToken(mockHttpServletRequest);

            assertThat(actualJwtAuthData).isNotNull();
            assertThat(actualJwtAuthData.accessToken()).isEqualTo(expectedAccessToken);
            assertThat(actualJwtAuthData.refreshToken()).isEqualTo(expectedRefreshToken);

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(mockHttpServletRequest).getRequestURL();
            verify(jwtService).getDecodedToken(any(String.class));
            verify(mockDecodedJWT).getSubject();
            verify(userService).getUserBy(givenEmail);
            verify(jwtService).createAccessToken(givenUser, givenRequestURL);
        }

        @Test
        void givenInvalidAuthorizationTokenWhenRefreshTokenThenInvalidAuthTokenException() {
            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Invalid Format");

            assertThatExceptionOfType(InvalidAuthTokenException.class)
                    .isThrownBy(
                            () -> authService.refreshToken(mockHttpServletRequest)
                    ).withMessage("Invalid authorization header");

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
        }

        @Test
        void givenEmptyAuthorizationTokenWhenRefreshTokenThenInvalidAuthTokenException() {
            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);

            assertThatExceptionOfType(InvalidAuthTokenException.class)
                    .isThrownBy(
                            () -> authService.refreshToken(mockHttpServletRequest)
                    ).withMessage("Invalid authorization header");

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
        }

        @Test
        void givenExpiredAuthorizationTokenWhenRefreshTokenThenInvalidAuthTokenException() {
            String givenRefreshToken = "refreshToken";

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);

            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + givenRefreshToken);
            when(jwtService.getDecodedToken(any(String.class))).thenThrow(JWTVerificationException.class);

            assertThatExceptionOfType(JWTVerificationException.class)
                    .isThrownBy(
                            () -> authService.refreshToken(mockHttpServletRequest)
                    );

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(jwtService).getDecodedToken(any(String.class));
        }

        @Test
        void givenAuthorizationTokenWithNotExistedEmailWhenRefreshTokenThenEntityNotFoundException() {
            String givenInvalidEmail = "test@example.com";
            String givenRefreshToken = "refreshToken";

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
            DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);

            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + givenRefreshToken);
            when(jwtService.getDecodedToken(any(String.class))).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(givenInvalidEmail);
            when(userService.getUserBy(givenInvalidEmail)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(
                            () -> authService.refreshToken(mockHttpServletRequest)
                    ).withMessage("User not found");

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(jwtService).getDecodedToken(any(String.class));
            verify(mockDecodedJWT).getSubject();
            verify(userService).getUserBy(givenInvalidEmail);
        }
    }
}