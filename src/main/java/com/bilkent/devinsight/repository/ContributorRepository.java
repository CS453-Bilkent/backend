package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributorRepository extends JpaRepository<Contributor, Long> {

}

