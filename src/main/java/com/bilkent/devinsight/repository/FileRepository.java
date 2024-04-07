package com.bilkent.devinsight.repository;

import com.bilkent.devinsight.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}

