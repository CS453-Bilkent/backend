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


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IssueService {


    @Value("${github.token}")
    private String githubToken;

    // Repositories
    private final IssueRepository issueRepository;
    private final UserRepositoryRelRepository userRepositoryRelRepository;

    // Services
    private final RepositoryService repositoryService;
    private final ContributorService contributorService;
    private final AuthService authService;


    public Set<Issue> getIssues(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        log.info("Getting issues for {}/{}", owner, repoName);

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();
        return issueRepository.findAllByRepository(repository);
    }


    public Set<Issue> scrapeIssues(QGithubScrape qGithubScrape) {
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
        return scrapeIssues(ghRepository, repository);
    }


    public Set<Issue> scrapeIssues(GHRepository ghRepository, final Repository repository) {

        Set<Issue> issues = new HashSet<>();

        List<GHIssue> ghIssues = null;

        try {
            ghIssues = ghRepository.getIssues(GHIssueState.ALL);
        } catch (IOException e) {
            log.error("Could not fetch issues.", e);
            throw new SomethingWentWrongException("Could not fetch issues.");
        }


        ghIssues.forEach(ghIssue -> {
            Optional<Issue> optionalIssue = parseGHIssue(ghIssue, repository);

            if (optionalIssue.isEmpty()) {
                return;
            }

            Issue issue = optionalIssue.get();
            issues.add(issue);
        });

        return issues;
    }

    public Optional<Issue> parseGHIssue(GHIssue ghIssue, final Repository repository) {
        String title;
        boolean isClosed;
        Date createdAt;
        Date closedAt;
        int severityRating;
        String url;
        int issueId;
        List<GHUser> assignees;
        try {
            title = ghIssue.getTitle();
            isClosed = ghIssue.getState().equals(GHIssueState.CLOSED);
            createdAt = ghIssue.getCreatedAt();
            closedAt = ghIssue.getClosedAt();
            severityRating = 1; // TODO: Example static value, adjust as needed
            url = ghIssue.getHtmlUrl().toString();
            assignees = ghIssue.getAssignees();


            if (url.contains("pull")) {
                return Optional.empty();
            }

            Pattern pattern = Pattern.compile(".*/(\\d+)$");
            Matcher matcher = pattern.matcher(url);

            if (!matcher.find()) {
                log.error("Could not parse pull request id, regex failed.");
                throw new SomethingWentWrongException("Could not parse pull request id.");
            }
            String issueIdString = matcher.group(1);
            issueId = Integer.parseInt(issueIdString);
        } catch (IOException e) {
            log.error("Could not fetch issue details.", e);
            throw new SomethingWentWrongException("Could not fetch issue details.");
        } catch (NumberFormatException e) {
            log.error("Could not parse issue id.", e);
            throw new SomethingWentWrongException("Could not parse issue id.");
        }

        Set<Contributor> parsedContributors = new HashSet<>();
        assignees.forEach(ghUser -> {
            Contributor contributor = contributorService.parseGHContributor(ghUser, repository);
            parsedContributors.add(contributor);
        });

        Optional<Issue> optIssue = issueRepository.findByIssueIdAndRepository(issueId, repository);
        Issue issue;

        if (optIssue.isPresent()) {
            issue = optIssue.get();
            issue.setTitle(title);
            issue.setClosedAt(closedAt);
            issue.setIsClosed(isClosed);
            issue.setSeverityRating(severityRating);
            issue.setUrl(url);
        } else {
            issue = Issue.builder()
                    .title(title)
                    .isClosed(isClosed)
                    .createdAt(createdAt)
                    .closedAt(closedAt)
                    .severityRating(severityRating)
                    .repository(repository)
                    .contributors(new HashSet<>())
                    .url(url)
                    .issueId(issueId)
                    .build();
        }
        Set<Contributor> contributors = issue.getContributors();
        contributors.addAll(parsedContributors);
        issue.setContributors(contributors);
        issue = issueRepository.save(issue);

//        Set<Issue> issues = repository.getIssues();
//        issues.add(issue);
//        repository.setIssues(issues);
//        repositoryRepository.save(repository);

        return Optional.of(issue);
    }

}
