package com.bilkent.devinsight.controller;


import com.bilkent.devinsight.entity.File;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.response.RFileCommitRank;
import com.bilkent.devinsight.response.RMostCommonlyChangedFiles;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @GetMapping("/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<File>>> getContributors(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<File> files = fileService.getFiles(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<File>>builder()
                        .data(files)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped files")
                        .build());
    }


    @GetMapping("/file-rankings/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<List<RFileCommitRank>>> getFileRankings(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        List<RFileCommitRank> files = fileService.getFileCommitRanks(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<List<RFileCommitRank>>builder()
                        .data(files)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped files")
                        .build());
    }

    @GetMapping("/most-commonly-changed-files/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<List<RMostCommonlyChangedFiles>>> getMostCommonlyChangedFiles(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        List<RMostCommonlyChangedFiles> files = fileService.findMostCommonlyChangedFilePairs(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<List<RMostCommonlyChangedFiles>>builder()
                        .data(files)
                        .status(HttpStatus.OK.value())
                        .message("Successfully gathered files")
                        .build());
    }



}
