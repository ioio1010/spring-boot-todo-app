package com.example.Todo.user;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;

public class UserSpecification {
    public static Specification<User> emailContains(final String email) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) return null;

            Expression<String> rootEmail = root.get("email");
            Expression<String> rootLowerEmail = criteriaBuilder.lower(rootEmail);

            return criteriaBuilder.like(rootLowerEmail, "%" +  email.toLowerCase() + "%");
        };
    }

    public static Specification<User> createdAtDateMatch(final LocalDate createdAt) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (createdAt == null) return null;

            Expression<LocalDate> rootCreatedAtDate = root.get("createdAt").as(LocalDate.class);

            return criteriaBuilder.equal(rootCreatedAtDate, createdAt);
        };
    }
}

