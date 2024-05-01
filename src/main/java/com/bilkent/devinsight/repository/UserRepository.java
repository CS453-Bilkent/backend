package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotNull String email);

    Optional<User> findByUsername(@NotNull String username);

    boolean existsByEmail(@NotNull  String email);

    boolean existsByUsername(@NotNull  String username);


}
