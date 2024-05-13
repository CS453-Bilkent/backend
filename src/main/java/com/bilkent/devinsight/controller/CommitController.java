package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Issue;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.RFileCommitRank;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.CommitService;
import com.bilkent.devinsight.service.IssueService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/commit")
public class CommitController {

    private final CommitService commitService;

    @GetMapping(path="/file-rankings")
    public ResponseEntity<List<RFileCommitRank>> getFileCommitRankings() {
        return null;
    }

    @GetMapping("/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<Commit>>> getCommits(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<Commit> issues = commitService.getCommits(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<Commit>>builder()
                        .data(issues)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched commits")
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape")
    public ResponseEntity<ApiResponse<Set<Commit>>> scrapeCommits(@Valid @RequestBody QGithubScrape qGithubScrape) {
        Set<Commit> issues = commitService.scrapeCommits(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<Set<Commit>>builder()
                        .data(issues)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped commits")
                        .build());
    }
}
