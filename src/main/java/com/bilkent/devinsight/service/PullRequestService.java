package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.*;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.RepositoryNotFoundException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.*;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PullRequestService {


    @Value("${github.token}")
    private String githubToken;

    // Repositories
    private final PullRequestRepository pullRequestRepository;
    private final UserRepositoryRelRepository userRepositoryRelRepository;

    // Services
    private final RepositoryService repositoryService;
    private final ContributorService contributorService;
    private final AuthService authService;

    public Set<PullRequest> getPullRequests(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();
        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        return pullRequestRepository.findAllByRepository(repository);
    }

    public Set<PullRequest> scrapePullRequests(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        Repository repository = repositoryService.getOrCreateRepository(owner, repoName);


        User user = authService.getCurrentUserEntity();

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

        return scrapePullRequests(ghRepository, repository);
    }

    public Set<PullRequest> scrapePullRequests(GHRepository ghRepository, final Repository repository) {
        Set<PullRequest> pullRequests = new HashSet<>();

        List<GHPullRequest> ghPullRequests = null;

        try {
            ghPullRequests = ghRepository.getPullRequests(GHIssueState.ALL);
        } catch (IOException e) {
            log.error("Could not fetch pull requests.", e);
            throw new SomethingWentWrongException("Could not fetch pull requests.");
        }

        ghPullRequests.forEach(ghPullRequest -> {
            PullRequest pullRequest = parseGHPullRequest(ghPullRequest, repository);
            pullRequests.add(pullRequest);
        });

        return pullRequests;
    }

    public PullRequest parseGHPullRequest(GHPullRequest ghPullRequest, final Repository repository) {
        String title;
        Date createdAt;
        Date mergedAt;
        int numberOfComments;
        boolean reviewed;
        int additions;
        int deletions;
        int size;
        String url;
        int pullRequestId;
        Set<GHUser> assignees;
        Set<GHUser> requestedReviewers;

        try {
            url = ghPullRequest.getHtmlUrl().toString();
            title = ghPullRequest.getTitle();
            createdAt = ghPullRequest.getCreatedAt();
            mergedAt = ghPullRequest.getMergedAt();
            numberOfComments = ghPullRequest.getCommentsCount();
            reviewed = ghPullRequest.getReviewComments() > 0;
            additions = ghPullRequest.getAdditions();
            deletions = ghPullRequest.getDeletions();
            size = ghPullRequest.getAdditions() + ghPullRequest.getDeletions();
            // Convert to set from list of ghPullRequest.getAssignees()
            assignees = new HashSet<>(ghPullRequest.getAssignees());
            requestedReviewers = new HashSet<>(ghPullRequest.getRequestedReviewers());
            Pattern pattern = Pattern.compile(".*/(\\d+)$");
            Matcher matcher = pattern.matcher(url);

            if (!matcher.find()) {
                log.error("Could not parse pull request id, regex failed.");
                throw new SomethingWentWrongException("Could not parse pull request id.");
            }

            String pullRequestIdString = matcher.group(1);
            pullRequestId = Integer.parseInt(pullRequestIdString);

        } catch (IOException e) {
            log.error("Could not fetch pull request details.", e);
            throw new SomethingWentWrongException("Could not fetch pull request details.");
        } catch (NumberFormatException e) {
            log.error("Could not parse pull request id.", e);
            throw new SomethingWentWrongException("Could not parse pull request id.");
        }
        Optional<PullRequest> optPullRequest = pullRequestRepository.
                findByPullRequestIdAndRepository(pullRequestId, repository);

        Set<Contributor> parsedContributors = new HashSet<>();
        assignees.forEach(ghUser -> {
            Contributor contributor = contributorService.parseGHContributor(ghUser, repository);
            parsedContributors.add(contributor);
        });
        log.info("Parsed PR: {} {}", pullRequestId, title);
//        Set<Contributor> parsedRequestedReviewers = new HashSet<>();
//        requestedReviewers.forEach(ghUser -> {
//            log.info("Requested reviewer: {}", ghUser);
//            Contributor contributor = contributorService.parseGHContributor(ghUser, repository);
//            log.info("Requested reviewer parsed: {}", contributor);
//            parsedRequestedReviewers.add(contributor);
//            log.info("Requested reviewers after add: {}", parsedRequestedReviewers);
//        });

        PullRequest pullRequest;

        if (optPullRequest.isPresent()) {
            log.info("Pull request already exists, updating.");
            log.info("Pull request id: {} title: {}", pullRequestId, title);
            pullRequest = optPullRequest.get();
            pullRequest.setTitle(title);
            pullRequest.setMergedAt(mergedAt);
            pullRequest.setNumberOfComments(numberOfComments);
            pullRequest.setReviewed(reviewed);
            pullRequest.setSize(size);
            pullRequest.setUrl(url);
            pullRequest.setAdditions(additions);
            pullRequest.setDeletions(deletions);
//            Set<Contributor> contributors = pullRequest.getAssignees();
//            contributors.addAll(parsedContributors);
//            pullRequest.setAssignees(contributors);
//            Set<Contributor> requestedReviewersSet = pullRequest.getRequestedReviewers();
//            log.info("Requested reviewers: {}", requestedReviewersSet);
////            parsedRequestedReviewers.forEach(contributor -> {
////                if (!requestedReviewersSet.contains(contributor)) {
////                    requestedReviewersSet.add(contributor);
////                }
////            });
//            log.info("Requested reviewers after update: {}", requestedReviewersSet);
//            pullRequest.setRequestedReviewers(requestedReviewersSet);
        } else {
            log.info("Creating new pull request.");
            log.info("Pull request id: {} title: {}", pullRequestId, title);
            log.info("Assignees: {}", parsedContributors);
//            log.info("Requested reviewers: {}", parsedRequestedReviewers);
            pullRequest = PullRequest.builder()
                    .title(title)
                    .createdAt(createdAt)
                    .mergedAt(mergedAt)
                    .numberOfComments(numberOfComments)
                    .reviewed(reviewed)
                    .size(size)
                    .url(url)
                    .additions(additions)
                    .deletions(deletions)
//                    .assignees(parsedContributors)
//                    .requestedReviewers(parsedRequestedReviewers)
                    .repository(repository)
                    .pullRequestId(pullRequestId)
                    .build();
        }
        pullRequest = pullRequestRepository.save(pullRequest);

        // Update repository pull requests
//        Set<PullRequest> pullRequests = repository.getPullRequests();
//        pullRequests.add(pullRequest);
//        repository.setPullRequests(pullRequests);
//        repositoryRepository.save(repository);

        return pullRequest;
    }

}
