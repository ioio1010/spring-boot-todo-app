package com.example.Todo.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Todo.user.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JWTService {
    private final JWTConfig jwtConfig;
    private final Algorithm algorithm;

    public JWTService(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret().getBytes());
    }

    public String createAccessToken(User user, String issuer) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(issuer);

        List<String> roles = user.getRoles().stream()
                .map((role) -> ( role.getRoleType().name()) )
                .collect(Collectors.toList());

        return createToken(
                user.getEmail(),
                new Date(System.currentTimeMillis() + (long) jwtConfig.getAccessTokenLiveMinutes() * 60 * 1000),
                issuer,
                "roles",
                roles
        );
    }

    public String createRefreshToken(User user, String issuer) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(issuer);

        return createToken(
                user.getEmail(),
                new Date(System.currentTimeMillis() + (long) jwtConfig.getRefreshTokenLiveMinutes() * 60 * 1000),
                issuer
        );
    }

    public String createToken(String subject, Date expiresAt, String issuer) throws JWTCreationException {
        Objects.requireNonNull(subject);
        Objects.requireNonNull(expiresAt);
        Objects.requireNonNull(issuer);

        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String createToken(
            String subject,
            Date expiresAt,
            String issuer,
            String claimKey,
            List<?> claims
    ) throws JWTCreationException {
        Objects.requireNonNull(subject);
        Objects.requireNonNull(expiresAt);
        Objects.requireNonNull(issuer);
        Objects.requireNonNull(claimKey);
        Objects.requireNonNull(claims);

        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .withClaim(claimKey, claims)
                .sign(algorithm);
    }

    public DecodedJWT getDecodedToken(String token) throws JWTVerificationException {
        Objects.requireNonNull(token);

        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }
}
