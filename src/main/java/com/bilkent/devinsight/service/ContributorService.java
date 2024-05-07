package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.*;
import com.bilkent.devinsight.exception.GithubConnectionException;
import com.bilkent.devinsight.exception.SomethingWentWrongException;
import com.bilkent.devinsight.repository.*;
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
public class ContributorService {


    @Value("${github.token}")
    private String githubToken;

    private final ContributorRepository contributorRepository;
    private final RepositoryService repositoryService;

    public Set<Contributor> scrapeContributors(QGithubScrape qGithubScrape) {
        String owner = qGithubScrape.getRepoOwner();
        String repoName = qGithubScrape.getRepoName();
        Repository repository = repositoryService.getOrCreateRepository(owner, repoName);
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

        Optional<Contributor> optContributor = contributorRepository.findByEmail(email);
        Contributor contributor;

        if (optContributor.isPresent()) {
            contributor = optContributor.get();
            contributor.setName(name);
            contributor.setUrl(url);
            contributor.setAvatarUrl(avatarUrl);
        } else {
            contributor = Contributor.builder()
                    .name(name)
                    .avatarUrl(avatarUrl)
                    .url(url)
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

}
