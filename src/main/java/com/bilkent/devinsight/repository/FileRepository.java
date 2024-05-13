package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.File;
import com.bilkent.devinsight.entity.Repository;
import com.bilkent.devinsight.response.RMostCommonlyChangedFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByShaAndRepository(String sha, Repository repository);

    Set<File> findAllByRepository(Repository repository);


}

