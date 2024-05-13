package com.bilkent.devinsight.service;

import com.bilkent.devinsight.entity.*;
import com.bilkent.devinsight.exception.RepositoryNotFoundException;
import com.bilkent.devinsight.repository.CommitRepository;
import com.bilkent.devinsight.repository.FileChangeRepository;
import com.bilkent.devinsight.repository.FileRepository;
import com.bilkent.devinsight.request.QGetRepository;
import com.bilkent.devinsight.response.RFileCommitRank;
import com.bilkent.devinsight.response.RMostCommonlyChangedFiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileService {



    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private EntityManager entityManager;

    // Repositories
    private final FileRepository fileRepository;
    private final FileChangeRepository fileChangeRepository;
    private final CommitRepository commitRepository;

    // Services
    private final RepositoryService repositoryService;
    private final ContributorService contributorService;
    private final PullRequestService pullRequestService;

    public Set<File> getFiles(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        log.info("Getting files for {}/{}", owner, repoName);

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        log.info("Repository found: {}/{} with id: {}", owner, repoName, repository.getId());

        return fileRepository.findAllByRepository(repository);
    }

    public File updateOrCreateFile(String sha, String name, String path, Repository repository) {
        Optional<File> fileOptional = fileRepository.findByShaAndRepository(sha, repository);
        File file;
        if (fileOptional.isPresent()) {
            file = fileOptional.get();
            file.setName(name);
            file.setPath(path);
        } else {
            file = File.builder()
                    .name(name)
                    .path(path)
                    .repository(repository)
                    .sha(sha)
                    .build();
        }

        file = fileRepository.save(file);
        return file;
    }



    public List<RFileCommitRank> getFileCommitRanks(QGetRepository qGetRepository) {
        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        log.info("Getting files for {}/{}", owner, repoName);

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();
        Set<File> files = fileRepository.findAllByRepository(repository); // Assumes that there's a method to fetch all files
        return files.stream().map(file -> RFileCommitRank.builder()
                        .fileId(file.getId())
                        .fileName(file.getName())
                        .commitCount(file.getFileChanges() != null ? file.getFileChanges().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }


    public List<RMostCommonlyChangedFiles> findMostCommonlyChangedFilePairs(QGetRepository qGetRepository) {

        String owner = qGetRepository.getRepoOwner();
        String repoName = qGetRepository.getRepoName();

        Optional<Repository> optRepository = repositoryService.getRepository(owner, repoName);

        if (optRepository.isEmpty()) {
            log.error("Repository {}/{} not found.", owner, repoName);
            throw new RepositoryNotFoundException();
        }

        Repository repository = optRepository.get();

        Set<Commit> commits = commitRepository.findAllByRepository(repository);
        log.info("Found {} commits for {}/{}", commits.size(), owner, repoName);

        Map<Pair, Long> filePairMap = new HashMap<>();

        for (Commit commit : commits) {
            List<File> filesInCommit = new ArrayList<>(commit.getChangedFiles());
            log.info("Files in commit: {}", filesInCommit.size());
            for (int i = 0; i < filesInCommit.size(); i++) {
                for (int j = i + 1; j < filesInCommit.size(); j++) {
                    Pair pair = new Pair(filesInCommit.get(i), filesInCommit.get(j));
                    filePairMap.put(pair, filePairMap.getOrDefault(pair, 0L) + 1);
                }
            }
        }

        log.info("File pair map size: {}", filePairMap.size());
        log.info("File pair map: {}", filePairMap);

        return filePairMap.entrySet().stream()
                .sorted(Map.Entry.<Pair, Long>comparingByValue().reversed())
                .map(entry -> new RMostCommonlyChangedFiles(
                        Arrays.asList(entry.getKey().file1.getName(), entry.getKey().file2.getName()),
                        Arrays.asList(entry.getKey().file1.getPath(), entry.getKey().file2.getPath()),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    private static class Pair {
        File file1;
        File file2;

        Pair(File file1, File file2) {
            this.file1 = file1;
            this.file2 = file2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return (Objects.equals(file1, pair.file1) && Objects.equals(file2, pair.file2)) ||
                    (Objects.equals(file1, pair.file2) && Objects.equals(file2, pair.file1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(file1.getId(), file2.getId()) + Objects.hash(file2.getId(), file1.getId());
        }
    }


    public FileChange parseGHFile(GHCommit.File ghFile, final File file) {

        String blobUrl;
        String filename;
        int linesAdded;
        int linesDeleted;
        int linesChanged;
        String patch;
        String previousFilename;
        String rawUrl;
        String sha;
        String status;
        blobUrl = ghFile.getBlobUrl().toString();
        filename = ghFile.getFileName();
        linesAdded = ghFile.getLinesAdded();
        linesDeleted = ghFile.getLinesDeleted();
        linesChanged = ghFile.getLinesChanged();
        patch = ghFile.getPatch();
        previousFilename = ghFile.getPreviousFilename();
        rawUrl = ghFile.getRawUrl().toString();
        sha = ghFile.getSha();
        status = ghFile.getStatus();

        log.info("Parsing file: {}", filename);
        log.info("Previous filename: {}", previousFilename);
        log.info("SHA: {}", sha);

        FileChange fileChange = FileChange.builder()
                .additions(linesAdded)
                .deletions(linesDeleted)
                .changes(linesChanged)
                .rawUrl(rawUrl)
                .status(status)
                .contentsUrl(blobUrl)
                .patch(patch)
                .file(file)
                .sha(sha)
                .build();

        fileChange = fileChangeRepository.save(fileChange);

        return fileChange;
    }



}
