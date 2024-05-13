package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Issue;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.PullRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/pull-request")
public class PullRequestController {

    private final PullRequestService pullRequestService;

    @GetMapping("/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<PullRequest>>> getPullRequests(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<PullRequest> pullRequests = pullRequestService.getPullRequests(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<PullRequest>>builder()
                        .data(pullRequests)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched pull requests")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape")
    public ResponseEntity<ApiResponse<Set<PullRequest>>> scrapePullRequests(@Valid @RequestBody QGithubScrape qGithubScrape) {
        Set<PullRequest> pullRequests = pullRequestService.scrapePullRequests(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<Set<PullRequest>>builder()
                        .data(pullRequests)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched pull requests")
                        .build());
    }

}
