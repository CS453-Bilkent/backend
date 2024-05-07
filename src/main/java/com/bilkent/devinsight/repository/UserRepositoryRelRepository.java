package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.UserRepositoryRel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryRelRepository extends JpaRepository<UserRepositoryRel, UUID> {


}
