package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Issue;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="scrape")
    public ResponseEntity<ApiResponse<Set<Commit>>> scrapePullRequests(@Valid @RequestBody QGithubScrape qGithubScrape) {
        Set<Commit> issues = commitService.scrapeCommits(qGithubScrape);

        return ResponseEntity.ok(
                ApiResponse.<Set<Commit>>builder()
                        .data(issues)
                        .status(HttpStatus.OK.value())
                        .message("Successfully scraped commits")
                        .build());
    }
}
