package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.ChangeEmailCode;
import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.enums.ChangeEmailCodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChangeEmailCodeRepository extends JpaRepository<ChangeEmailCode, Long> {

    List<ChangeEmailCode> findByUser(User user);

    List<ChangeEmailCode> findByUserAndValid(User user, Boolean valid);

    List<ChangeEmailCode> findByUserAndValidAndUsed(User user, Boolean valid, Boolean used);

    Optional<ChangeEmailCode> findByUserAndValidAndUsedAndChangeEmailCodeType(User user,
                                                                              Boolean valid,
                                                                              Boolean used,
                                                                              ChangeEmailCodeType type);

    void deleteByUser(User user);


}
