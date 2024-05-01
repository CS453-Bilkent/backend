package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.Issue;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.service.GithubService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/github")
public class GithubController {

    private final GithubService githubService;



//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape")
//    public ResponseEntity<ApiResponse<List<GHCommit>>> login(@Valid @RequestBody ReqGithubScrape reqGithubScrape) {
//        List<GHCommit> commits = githubService.getFileCommitCounts(reqGithubScrape);
//
//        return ResponseEntity.ok(
//                ApiResponse.<List<GHCommit>>builder()
//                        .data(commits)
//                        .status(HttpStatus.OK.value())
//                        .message("Successfully fetched commits")
//                        .build());
//
//    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape/commits")
    public ResponseEntity<ApiResponse<List<Commit>>> scrapeCommits(@Valid @RequestBody QGithubScrape qGithubScrape) {
        List<Commit> commits = githubService.scrapeCommits(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<List<Commit>>builder()
                        .data(commits)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched commits")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape/pull-requests")
    public ResponseEntity<ApiResponse<List<PullRequest>>> scrapePullRequests(@Valid @RequestBody QGithubScrape qGithubScrape) {
        List<PullRequest> pullRequests = githubService.scrapePullRequests(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<List<PullRequest>>builder()
                        .data(pullRequests)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched pull requests")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape/issues")
    public ResponseEntity<ApiResponse<List<Issue>>> scrapeIssues(@Valid @RequestBody QGithubScrape qGithubScrape) {
        List<Issue> issues = githubService.scrapeIssues(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<List<Issue>>builder()
                        .data(issues)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched issues")
                        .build());

    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape/contributors")
    public ResponseEntity<ApiResponse<List<Contributor>>> scrapeContributors(@Valid @RequestBody QGithubScrape qGithubScrape) {
        List<Contributor> contributors = githubService.scrapeContributors(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<List<Contributor>>builder()
                        .data(contributors)
                        .status(HttpStatus.OK.value())
                        .message("Successfully fetched contributors")
                        .build());
    }




}
