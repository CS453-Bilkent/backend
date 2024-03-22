package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.ResetPasswordCode;
import com.bilkent.devinsight.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPasswordCode, Long> {

    Optional<ResetPasswordCode> findByUser(User user);

    void deleteByUser(User user);


}
