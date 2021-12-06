package com.example.Todo.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "java-jwt")
@Data
public class JWTConfig {
    private String secret;
    private int accessTokenLiveMinutes;
    private int refreshTokenLiveMinutes;
}
