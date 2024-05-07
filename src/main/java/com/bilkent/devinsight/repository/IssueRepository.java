package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Long countByClosedByAndIsClosedTrue(Contributor contributor);

    Optional<Issue> findByIssueIdAndRepository(Integer issueId, Repository repository);

}
