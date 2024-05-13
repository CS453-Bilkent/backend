package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.File;
import com.bilkent.devinsight.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findAllByChangedFilesContains(File file);

    Long countByContributor(Contributor contributor);

    Optional<Commit> findByHashAndRepository(String hash, Repository repository);

    Set<Commit> findAllByRepository(Repository repository);

}
