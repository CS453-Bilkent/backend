package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.RCollaborativePRCount;
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

    @GetMapping("most-active-discussions/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<PullRequest>>> getMostActiveDiscussions(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<PullRequest> pullRequests = pullRequestService.findMostActiveDiscussions(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<PullRequest>>builder()
                        .data(pullRequests)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched pull requests")
                        .build());
    }

    @GetMapping("collaborative-prs/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<RCollaborativePRCount>>> getCollaborativePullRequests(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<RCollaborativePRCount> pullRequests = pullRequestService.countCollaborativePullRequests(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<RCollaborativePRCount>>builder()
                        .data(pullRequests)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched pull requests")
                        .build());
    }

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

    @GetMapping("average-merge-time/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Double>> getAverageMergeTime(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Double averageMergeTime = pullRequestService.calculateAverageMergeTime(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Double>builder()
                        .data(averageMergeTime)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched average merge time")
                        .build());
    }

    @GetMapping("average-comments-per-pull-request/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Double>> getAverageCommentsPerPullRequest(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Double averageCommentsPerPullRequest = pullRequestService.calculateAverageCommentsPerPullRequest(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Double>builder()
                        .data(averageCommentsPerPullRequest)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched average comments per pull request")
                        .build());
    }

    @GetMapping("average-pull-request-size/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Double>> getAveragePullRequestSize(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Double averagePullRequestSize = pullRequestService.calculateAveragePullRequestSize(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Double>builder()
                        .data(averagePullRequestSize)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched average pull request size")
                        .build());
    }

    @GetMapping("review-coverage/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Double>> getReviewCoverage(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Double reviewCoverage = pullRequestService.calculateReviewCoverage(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Double>builder()
                        .data(reviewCoverage)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched review coverage")
                        .build());
    }

}
