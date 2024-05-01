package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.File;
import com.bilkent.devinsight.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Long countByClosedByAndIsClosedTrue(Contributor contributor);

}
