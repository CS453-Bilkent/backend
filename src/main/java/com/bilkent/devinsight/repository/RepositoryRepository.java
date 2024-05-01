package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {



}
