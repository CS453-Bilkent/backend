package com.bilkent.devinsight.service;

import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.Issue;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GithubService {


    @Value("${github.token}")
    private String githubToken;

    private final CommitRepository commitRepository;
    private final ContributorRepository contributorRepository;
    private final IssueRepository issueRepository;
    private final FileRepository fileRepository;
    private final PullRequestRepository pullRequestRepository;
    private final RepositoryRepository repositoryRepository;



    public void scrapeAll(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        System.out.println("GithubService.getFileCommitCounts");

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        scrapeContributors(repository);
        scrapeCommits(repository);
        scrapeFiles(repository);
        scrapeIssues(repository);
        scrapePullRequests(repository);

        return;
    }

    public List<Contributor> scrapeContributors(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        return scrapeContributors(repository);
    }

    public List<Commit> scrapeCommits(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        return scrapeCommits(repository);
    }

    public List<Issue> scrapeIssues(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        return scrapeIssues(repository);
    }

    public List<PullRequest> scrapePullRequests(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        return scrapePullRequests(repository);
    }

    public void scrapeFiles(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        GHRepository repository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            repository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        scrapeFiles(repository);
    }


    private List<Contributor> scrapeContributors(GHRepository repository) {
        List<Contributor> contributors = new ArrayList<>();

        PagedIterable<GHRepository.Contributor> ghContributors = null;

        try {
            ghContributors = repository.listContributors();
        } catch (IOException e) {
            log.error("Could not fetch contributors.", e);
            throw new SomethingWentWrongException("Could not fetch contributors.");
        }

        // Fetch and save contributors
        ghContributors.forEach(ghUser -> {
            Contributor contributor = parseGHContributor(ghUser);
            contributors.add(contributor);
        });
        return contributors;
    }

    public List<Commit> scrapeCommits(GHRepository repository) {
        List<Commit> commits = new ArrayList<>();

        PagedIterable<GHCommit> ghCommits = repository.listCommits();

        // Fetch and save commits
        ghCommits.forEach(ghCommit -> {
            Commit commit = parseGHCommit(ghCommit);
            commits.add(commit);
        });

        return commits;
    }

    public void scrapeFiles(GHRepository repository) {
        // Fetch and save file structure - might need additional processing depending on API capabilities
    }

    public List<Issue> scrapeIssues(GHRepository repository) {

        List<Issue> issues = new ArrayList<>();

        List<GHIssue> ghIssues = null;

        try {
            ghIssues = repository.getIssues(GHIssueState.ALL);
        } catch (IOException e) {
            log.error("Could not fetch issues.", e);
            throw new SomethingWentWrongException("Could not fetch issues.");
        }


        ghIssues.forEach(ghIssue -> {
            Issue issue = parseGHIssue(ghIssue);
            issues.add(issue);
        });

        return issues;
    }

    public List<PullRequest> scrapePullRequests(GHRepository repository) {
        List<PullRequest> pullRequests = new ArrayList<>();

        List<GHPullRequest> ghPullRequests = null;

        try {
            ghPullRequests = repository.getPullRequests(GHIssueState.ALL);
        } catch (IOException e) {
            log.error("Could not fetch pull requests.", e);
            throw new SomethingWentWrongException("Could not fetch pull requests.");
        }

        ghPullRequests.forEach(ghPullRequest -> {
            PullRequest pullRequest = parseGHPullRequest(ghPullRequest);
            pullRequests.add(pullRequest);
        });

        return pullRequests;
    }

    public Issue parseGHIssue(GHIssue ghIssue) {
        Issue issue = null;
        try {
            issue = Issue.builder()
                    .title(ghIssue.getTitle())
                    .isClosed(ghIssue.getState().equals(GHIssueState.CLOSED))
                    .createdAt(ghIssue.getCreatedAt())
                    .closedAt(ghIssue.getClosedAt() != null ? ghIssue.getClosedAt() : null)
                    .severityRating(1) // Example static value, adjust as needed
                    // You need to fetch and set the Contributor and Repository
                    .build();
        } catch (IOException e) {
            log.error("Could not fetch issue details.", e);
            throw new SomethingWentWrongException("Could not fetch issue details.");
        }
        issue = issueRepository.save(issue);
        return issue;
    }

    public PullRequest parseGHPullRequest(GHPullRequest ghPullRequest) {
        PullRequest pullRequest = null;
        try {
            pullRequest = PullRequest.builder()
                    .title(ghPullRequest.getTitle())
                    .createdAt(LocalDateTime.parse(ghPullRequest.getCreatedAt().toString()))
                    .mergedAt(ghPullRequest.getMergedAt() != null ? LocalDateTime.parse(ghPullRequest.getMergedAt().toString()) : null)
                    .numberOfComments(ghPullRequest.getCommentsCount())
                    .reviewed(ghPullRequest.getReviewComments() > 0)
                    .size(ghPullRequest.getAdditions() + ghPullRequest.getDeletions())
                    // You need to fetch and set the Issue and Repository
                    .build();
        } catch (IOException e) {
            log.error("Could not fetch pull request details.", e);
            throw new SomethingWentWrongException("Could not fetch pull request details.");
        }
        pullRequest = pullRequestRepository.save(pullRequest);
        return pullRequest;
    }

    public Commit parseGHCommit(GHCommit ghCommit) {
        Commit commit = null;
        try {
            commit = Commit.builder()
                    .timestamp(ghCommit.getCommitDate())
                    .hash(ghCommit.getSHA1())
                    .commitTime(ghCommit.getCommitDate())
                    // You need to fetch and set the Contributor and Repository
                    .build();
        } catch (IOException e) {
            log.error("Could not fetch commit details.", e);
            throw new SomethingWentWrongException("Could not fetch commit details.");
        }
        commit = commitRepository.save(commit);
        return commit;
    }

    public Contributor parseGHContributor(GHUser ghUser) {
        Contributor contributor = null;
        try {
            contributor = Contributor.builder()
                    .name(ghUser.getName())
                    .email(ghUser.getEmail())
                    .build();
        } catch (IOException e) {
            log.error("Could not fetch contributor details.", e);
            throw new SomethingWentWrongException("Could not fetch contributor details.");
        }
        contributor = contributorRepository.save(contributor);
        return contributor;
    }

}
