package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {

    Optional<Contributor> findByEmail(String email);

}

