package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.*;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.RepositoryNotFoundException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.CommitRepository;
import com.bilkent.devinsight.repository.UserRepositoryRelRepository;
import com.bilkent.devinsight.request.QGetRepository;
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
    private final UserRepositoryRelRepository userRepositoryRelRepository;


    // Services
    private final RepositoryService repositoryService;
    private final ContributorService contributorService;
    private final PullRequestService pullRequestService;
    private final AuthService authService;
    private final FileService fileService;


    public Set<Commit> getCommits(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        log.info("Getting commits for {}/{}", owner, repoName);

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        log.info("Repository found: {}/{} with id: {}", owner, repoName, repository.getId());

        return commitRepository.findAllByRepository(repository);
    }

    public Set<Commit> scrapeCommits(QGithubScrape qGithubScrape) {
        User user = authService.getCurrentUserEntity();

        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();

        log.info("Scraping commits for {}/{}", owner, repoName);

        Repository repository = repositoryService.getOrCreateRepository(owner, repoName);

        UserRepositoryRel userRepositoryRel = UserRepositoryRel.builder()
                .repository(repository)
                .build();
        userRepositoryRel = userRepositoryRelRepository.save(userRepositoryRel);
        Set<UserRepositoryRel> userRepositories = user.getRepositories();
        userRepositories.add(userRepositoryRel);
        user.setRepositories(userRepositories);
        authService.save(user);

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

        // Get the first 10 commits
        ghCommits = ghCommits.withPageSize(10);

        final int[] index = {0};
        // Fetch and save commits
        ghCommits.forEach(ghCommit -> {
            if (index[0] >= 30) {
                return;
            }
            Commit commit = parseGHCommit(ghCommit, repository);
            commits.add(commit);
            index[0]++;
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
        PagedIterable<GHCommit.File> changedFiles;
        try {
            timestamp = ghCommit.getCommitDate();
            hash = ghCommit.getSHA1();
            commitTime = ghCommit.getCommitDate();
            url = ghCommit.getHtmlUrl().toString();
            committer = ghCommit.getCommitter();
            scrapedPullRequests = ghCommit.listPullRequests();
            changedFiles = ghCommit.listFiles();
        } catch (IOException e) {
            log.error("Could not fetch commit details.", e);
            throw new SomethingWentWrongException("Could not fetch commit details.");
        }

        log.info("Scraping commit: {}", hash);

//        Optional<Commit> optCommit = commitRepository.findByHashAndRepository(hash, repository);
        Optional<Commit> optCommit = Optional.empty();
        Commit commit;

        Contributor contributor = contributorService.parseGHContributor(committer, repository);


        Set<PullRequest> pullRequests = new HashSet<>();
        scrapedPullRequests.forEach(ghPullRequest -> {
            PullRequest pullRequest = pullRequestService.parseGHPullRequest(ghPullRequest, repository);
            pullRequests.add(pullRequest);
        });

        Set<File> newFiles = new HashSet<>();
        Set<FileChange> fileChanges = new HashSet<>();
        changedFiles.forEach(ghFile -> {
            File file = fileService.updateOrCreateFile(ghFile.getSha(), ghFile.getFileName(), ghFile.getRawUrl().getPath(), repository);
            FileChange fileChange = fileService.parseGHFile(ghFile, file);
            fileChanges.add(fileChange);
            newFiles.add(file);
        });


        // TODO: Set contributor, changedFiles
        if (optCommit.isPresent()) {
            commit = optCommit.get();
            commit.setTimestamp(timestamp);
            commit.setCommitTime(commitTime);
            commit.setUrl(url);
            commit.setHash(hash);
            commit.setContributor(contributor);
            Set<File> files = commit.getChangedFiles();
            files.addAll(newFiles);
            commit.setChangedFiles(files);


        } else {
            commit = Commit.builder()
                    .timestamp(timestamp)
                    .hash(hash)
                    .commitTime(commitTime)
                    .repository(repository)
                    .contributor(contributor)
                    .changedFiles(newFiles)
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
