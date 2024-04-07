package com.bilkent.devinsight.service;

import com.bilkent.devinsight.repository.CommitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CommitService {

    private final CommitRepository commitRepository;
    private final FileService fileService;


}
