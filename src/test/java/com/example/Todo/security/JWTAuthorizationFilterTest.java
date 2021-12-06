package com.example.Todo.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Todo.auth.jwt.JWTService;
import com.example.Todo.exception.InvalidAuthTokenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@ExtendWith(MockitoExtension.class)
class JWTAuthorizationFilterTest {
    @Mock
    private JWTService jwtService;

    @InjectMocks
    private JWTAuthorizationFilter jwtAuthorizationFilter;

    @AfterEach
    public void clearAuthenticationFromContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Nested
    class DoFilterInternal {
        @Test
        void givenValidAuthorizationTokenWhenDoFilterInternalThenContextWithAuthentication() throws ServletException, IOException {
            String givenAccessToken = "accessToken";
            String givenTokenClaimKey = "roles";
            String[] givenTokenClaims = new String[]{ "USER" };

            String expectedEmail = "test@example.com";
            List<SimpleGrantedAuthority> expectedAuthorities = List.of(new SimpleGrantedAuthority("USER"));

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
            HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
            FilterChain mockFilterChain = mock(FilterChain.class);
            DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
            Claim mockClaim = mock(Claim.class);

            doNothing().when(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + givenAccessToken);
            when(jwtService.getDecodedToken(any(String.class))).thenReturn(mockDecodedJWT);
            when(mockDecodedJWT.getSubject()).thenReturn(expectedEmail);
            when(mockDecodedJWT.getClaim(givenTokenClaimKey)).thenReturn(mockClaim);
            when(mockClaim.asArray(String.class)).thenReturn(givenTokenClaims);

            jwtAuthorizationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
            Authentication actualAuthentication = SecurityContextHolder.getContext().getAuthentication();

            assertThat(actualAuthentication.getPrincipal()).isEqualTo(expectedEmail);
            assertThat(actualAuthentication.getAuthorities()).isEqualTo(expectedAuthorities);

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(jwtService).getDecodedToken(any(String.class));
            verify(mockDecodedJWT).getSubject();
            verify(mockDecodedJWT).getClaim(givenTokenClaimKey);
            verify(mockClaim).asArray(String.class);
            verify(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
        }

        @Test
        void givenInvalidAuthorizationTokenWhenDoFilterInternalThenContextWithoutAuthentication() throws ServletException, IOException {
            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
            HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
            FilterChain mockFilterChain = mock(FilterChain.class);

            doNothing().when(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Invalid format");

            jwtAuthorizationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
            Authentication actualAuthentication = SecurityContextHolder.getContext().getAuthentication();

            assertThat(actualAuthentication).isNull();

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
        }

        @Test
        void givenExpiredAuthorizationTokenWhenDoFilterInternalThenInvalidAuthTokenException() {
            String expectedAccessToken = "accessToken";

            HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
            HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
            FilterChain mockFilterChain = mock(FilterChain.class);

            when(mockHttpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + expectedAccessToken);
            when(jwtService.getDecodedToken(any(String.class))).thenThrow(JWTVerificationException.class);

            assertThatExceptionOfType(InvalidAuthTokenException.class)
                    .isThrownBy(
                            () -> jwtAuthorizationFilter.doFilterInternal(
                                    mockHttpServletRequest,
                                    mockHttpServletResponse,
                                    mockFilterChain
                            )
                    ).withMessage("Invalid authorization header");

            verify(mockHttpServletRequest).getHeader(AUTHORIZATION);
            verify(jwtService).getDecodedToken(any(String.class));
        }
    }
}