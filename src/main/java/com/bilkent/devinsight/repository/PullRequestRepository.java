package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    List<PullRequest> findAllPullRequestsByRepository(Repository repository);

    Optional<PullRequest> findByPullRequestIdAndRepository(Integer pullRequestId, Repository repository);

    Set<PullRequest> findAllByRepository(Repository repository);

}
