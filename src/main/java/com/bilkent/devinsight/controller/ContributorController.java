package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.Issue;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.ContributorService;
import com.bilkent.devinsight.service.IssueService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/contributor")
public class ContributorController {

    private final ContributorService contributorService;


    @GetMapping("/{repoOwner}/{repoName}")
    public ResponseEntity<ApiResponse<Set<Contributor>>> getContributors(
            @PathVariable("repoOwner") String repoOwner,
            @PathVariable("repoName") String repoName) {
        QGetRepository qGetRepository = QGetRepository.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .build();
        Set<Contributor> contributors = contributorService.getContributors(qGetRepository);

        return ResponseEntity.ok(
                ApiResponse.<Set<Contributor>>builder()
                        .data(contributors)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped contributors")
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape")
    public ResponseEntity<ApiResponse<Set<Contributor>>> scrapePullRequests(@Valid @RequestBody QGithubScrape qGithubScrape) {
        Set<Contributor> contributors = contributorService.scrapeContributors(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<Set<Contributor>>builder()
                        .data(contributors)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped contributors")
                        .build());
    }

}
