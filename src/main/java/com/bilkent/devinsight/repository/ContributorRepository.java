package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {

    Set<Contributor> findAllByRepository(Repository repository);
    Optional<Contributor> findByEmail(String email);

}

