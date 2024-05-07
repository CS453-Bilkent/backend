package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.Commit;
import com.bilkent.devinsight.entity.Contributor;
import com.bilkent.devinsight.entity.PullRequest;
import com.bilkent.devinsight.entity.Repository;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.CommitRepository;
import com.bilkent.devinsight.repository.RepositoryRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommitService {


    @Value("${github.token}")
    private String githubToken;

    // Repositories
    private final CommitRepository commitRepository;

    // Services
    private final RepositoryService repositoryService;
    private final ContributorService contributorService;
    private final PullRequestService pullRequestService;

    public Set<Commit> scrapeCommits(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();

        log.info("Scraping commits for {}/{}", owner, repoName);

        Repository repository = repositoryService.getOrCreateRepository(owner, repoName);
        GHRepository ghRepository = null;

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            ghRepository = github.getRepository(owner + "/" + repoName);
        } catch (IOException e) {
            log.error("Could not connect to the Github service.", e);
            throw new GithubConnectionException("Could not connect to the Github service.");
        }

        return scrapeCommits(ghRepository, repository);
    }


    public Set<Commit> scrapeCommits(GHRepository ghRepository, final Repository repository) {
        Set<Commit> commits = new HashSet<>();

        PagedIterable<GHCommit> ghCommits = ghRepository.listCommits();

        // Fetch and save commits
        ghCommits.forEach(ghCommit -> {
            Commit commit = parseGHCommit(ghCommit, repository);
            commits.add(commit);
        });

//        repository.setCommits(commits);
//        repositoryRepository.save(repository);

        return commits;
    }


    public Commit parseGHCommit(GHCommit ghCommit, final Repository repository) {
        Date timestamp;
        String hash;
        Date commitTime;
        String url;
        GHUser committer;
        PagedIterable<GHPullRequest> scrapedPullRequests;
        try {
            timestamp = ghCommit.getCommitDate();
            hash = ghCommit.getSHA1();
            commitTime = ghCommit.getCommitDate();
            url = ghCommit.getHtmlUrl().toString();
            committer = ghCommit.getCommitter();
            scrapedPullRequests = ghCommit.listPullRequests();
        } catch (IOException e) {
            log.error("Could not fetch commit details.", e);
            throw new SomethingWentWrongException("Could not fetch commit details.");
        }

        Optional<Commit> optCommit = commitRepository.findByHashAndRepository(hash, repository);
        Commit commit;

        Contributor contributor = contributorService.parseGHContributor(committer, repository);

        Set<PullRequest> pullRequests = new HashSet<>();
        scrapedPullRequests.forEach(ghPullRequest -> {
            PullRequest pullRequest = pullRequestService.parseGHPullRequest(ghPullRequest, repository);
            pullRequests.add(pullRequest);
        });

        // TODO: Set contributor, changedFiles
        if (optCommit.isPresent()) {
            commit = optCommit.get();
            commit.setTimestamp(timestamp);
            commit.setCommitTime(commitTime);
            commit.setUrl(url);
            commit.setHash(hash);
            commit.setContributor(contributor);
        } else {
            commit = Commit.builder()
                    .timestamp(timestamp)
                    .hash(hash)
                    .commitTime(commitTime)
                    .repository(repository)
                    .contributor(contributor)
                    .pullRequests(new HashSet<>())
                    .url(url)
                    .build();
        }

        Set<PullRequest> existingPullRequests = commit.getPullRequests();
        existingPullRequests.addAll(pullRequests);
        commit.setPullRequests(existingPullRequests);
        commit = commitRepository.save(commit);

        // Update repository commits
//        Set<Commit> commits = repository.getCommits();
//        commits.add(commit);
//        repository.setCommits(commits);
//        repositoryRepository.save(repository);

        return commit;
    }


}
