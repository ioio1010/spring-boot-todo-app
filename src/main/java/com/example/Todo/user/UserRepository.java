package com.example.Todo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query(
            nativeQuery=true,
            value="SELECT DISTINCT u.* FROM users u INNER JOIN todo_items ti ON u.id = ti.user_id"
    )
    List<User> findAllByTodayItemsExist();
}
