package com.example.Todo.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration(proxyBeanMethods = false)
public class FiltersConfiguration {

    @Bean
    public FilterRegistrationBean<JWTAuthorizationFilter> registration(JWTAuthorizationFilter jwtAuthorizationFilter) {
        FilterRegistrationBean<JWTAuthorizationFilter> registration = new FilterRegistrationBean<>(jwtAuthorizationFilter);
        registration.setEnabled(false);
        return registration;
    }
}