package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.Repository;
import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.UserRepositoryRel;
import com.bilkent.devinsight.repository.RepositoryRepository;
import com.bilkent.devinsight.repository.UserRepository;
import com.bilkent.devinsight.repository.UserRepositoryRelRepository;
import com.bilkent.devinsight.request.QAddRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserRepositoryRelRepository userRepositoryRelRepository;
    private final UserRepository userRepository;

    private final AuthService authService;


    public Optional<Repository> getRepository(String owner, String repoName) {
        return repositoryRepository.findByNameAndOwner(repoName, owner);
    }

    public Set<Repository> getRepositories() {
        User user = authService.getCurrentUserEntity();
        Set<Repository> repositories = new HashSet<>();
        user.getRepositories().forEach(userRepositoryRel -> repositories.add(userRepositoryRel.getRepository()));
        return repositories;
    }

    public Repository addRepository(QAddRepository qAddRepository) {

        User user = authService.getCurrentUserEntity();

        String owner = qAddRepository.getRepoOwner();
        String repoName = qAddRepository.getRepoName();

        Repository repository = getOrCreateRepository(owner, repoName);
        UserRepositoryRel userRepositoryRel = UserRepositoryRel.builder()
                .repository(repository)
                .build();

        userRepositoryRel = userRepositoryRelRepository.save(userRepositoryRel);

        Set<UserRepositoryRel> userRepos = user.getRepositories();
        userRepos.add(userRepositoryRel);
        user.setRepositories(userRepos);

        userRepository.save(user);

        return repository;
    }

    public Repository getOrCreateRepository(String owner, String repoName) {
        Optional<Repository> optRepository = repositoryRepository.findByNameAndOwner(repoName, owner);

        log.info("Repository {}/{} searching.", owner, repoName);
        Repository repository;

        if (optRepository.isPresent()) {
            log.info("Repository {}/{} found.", owner, repoName);
            repository = optRepository.get();
        } else {
            log.info("Repository {}/{} not found. Creating new repository.", owner, repoName);
            repository = Repository.builder()
                    .issues(new HashSet<>())
                    .pullRequests(new HashSet<>())
                    .contributors(new HashSet<>())
                    .commits(new HashSet<>())
                    .files(new HashSet<>())
                    .url("https://github.com/" + owner + "/" + repoName)
                    .name(repoName)
                    .owner(owner)
                    .build();
            repository = repositoryRepository.save(repository);
        }

        return repository;
    }
}
