package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.File;
import com.bilkent.devinsight.entity.FileChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileChangeRepository extends JpaRepository<FileChange, Long> {



}

