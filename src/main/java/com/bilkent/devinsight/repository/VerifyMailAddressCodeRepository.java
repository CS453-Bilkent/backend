package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.VerifyMailAddressCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VerifyMailAddressCodeRepository extends JpaRepository<VerifyMailAddressCode, Long> {

    List<VerifyMailAddressCode> findByUser(User user);

    List<VerifyMailAddressCode> findByUserAndValid(User user, Boolean valid);

    List<VerifyMailAddressCode> findByUserAndValidAndUsed(User user, Boolean valid, Boolean used);

    List<VerifyMailAddressCode> findByUserAndValidAndUsedAndExpireDateAfter(User user,
                                                                            Boolean valid,
                                                                            Boolean used,
                                                                            Date expireDate);



    void deleteByUser(User user);


}
