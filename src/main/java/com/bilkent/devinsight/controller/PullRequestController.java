package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.PullRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/pull-request")
public class PullRequestController {

    private final PullRequestService pullRequestService;

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
