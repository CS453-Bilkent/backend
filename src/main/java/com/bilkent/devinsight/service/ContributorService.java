package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.*;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.RepositoryNotFoundException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.*;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.request.QGithubScrape;
import com.bilkent.devinsight.response.RDeveloperEffectiveness;
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
public class ContributorService {


    @Value("${github.token}")
    private String githubToken;

    private final ContributorRepository contributorRepository;
    private final UserRepositoryRelRepository userRepositoryRelRepository;

    private final RepositoryService repositoryService;
    private final AuthService authService;

    public Set<Contributor> getContributors(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        return contributorRepository.findAllByRepository(repository);
    }

    public Set<Contributor> scrapeContributors(QGithubScrape qGithubScrape) {
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

        return scrapeContributors(ghRepository, repository);
    }

    private Set<Contributor> scrapeContributors(GHRepository ghRepository, Repository repository) {
        Set<Contributor> contributors = new HashSet<>();

        PagedIterable<GHRepository.Contributor> ghContributors = null;

        try {
            ghContributors = ghRepository.listContributors();
        } catch (IOException e) {
            log.error("Could not fetch contributors.", e);
            throw new SomethingWentWrongException("Could not fetch contributors.");
        }

        // Fetch and save contributors
        ghContributors.forEach(ghUser -> {
            Contributor contributor = parseGHContributor(ghUser, repository);
            contributors.add(contributor);
        });
        return contributors;
    }

    public Contributor parseGHContributor(GHUser ghUser, final Repository repository) {
        String name;
        String email;
        String url;
        String avatarUrl;
        try {
            name = ghUser.getName();
            email = ghUser.getEmail();
            url = ghUser.getHtmlUrl().toString();
            avatarUrl = ghUser.getAvatarUrl();
        } catch (IOException e) {
            log.error("Could not fetch contributor details.", e);
            throw new SomethingWentWrongException("Could not fetch contributor details.");
        }
        log.info("Contributor: " + name + " " + email + " " + url + " " + avatarUrl);

        Optional<Contributor> optContributor = contributorRepository.findByEmail(email);
        log.info("Optional contributor: " + optContributor);
        Contributor contributor;


        if (optContributor.isPresent()) {
            log.info("Contributor already exists");
            contributor = optContributor.get();
            contributor.setName(name);
            contributor.setUrl(url);
            contributor.setAvatarUrl(avatarUrl);
        } else {
            log.info("Contributor does not exist");
            contributor = Contributor.builder()
                    .name(name)
                    .avatarUrl(avatarUrl)
                    .url(url)
                    .repository(repository)
                    .email(email)
                    .build();
        }

//        // Update contributor repositories
//        List<Repository> repositories = contributor.getRepositories();
//        repositories.add(repository);
//        contributor.setRepositories(repositories);
        contributor = contributorRepository.save(contributor);

        // Update repository contributors
//        Set<Contributor> contributors = repository.getContributors();
//        contributors.add(contributor);
//        repository.setContributors(contributors);
//        repositoryRepository.save(repository);

        return contributor;
    }

    public Set<RDeveloperEffectiveness> calculateDeveloperEffectiveness(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        Set<Contributor> contributors = contributorRepository.findAllByRepository(repository); // Assumes a method to fetch all contributors
        return contributors.stream().map(this::mapToDeveloperEffectiveness)
                .collect(Collectors.toSet());
    }

    private RDeveloperEffectiveness mapToDeveloperEffectiveness(Contributor contributor) {
        long commitCount = contributor.getCommits() != null ? contributor.getCommits().size() : 0;
        long closedIssuesCount = calculateClosedIssues(contributor); // Implement this based on your domain logic
        long bugsFixed = calculateBugsFixed(contributor); // Implement this based on your domain logic

        return RDeveloperEffectiveness.builder()
                .contributor(contributor)
                .commitCount(commitCount)
                .closedIssuesCount(closedIssuesCount)
                .bugsFixed(bugsFixed)
                .build();
    }

    // Placeholder for closed issues calculation
    private long calculateClosedIssues(Contributor contributor) {
        // Custom logic to calculate the number of closed issues
        return 0; // Return actual value
    }

    // Placeholder for bugs fixed calculation
    private long calculateBugsFixed(Contributor contributor) {
        // Custom logic to calculate the number of bugs fixed
        return 0; // Return actual value
    }

}
