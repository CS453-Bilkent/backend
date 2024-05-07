package com.bilkent.devinsight.service;

import com.bilkent.devinsight.repository.CommitRepository;
import com.bilkent.devinsight.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository fileRepository;



}
