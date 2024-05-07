package com.bilkent.devinsight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "repositories")
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Repository extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String owner;

    @NotNull
    private String url;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Issue> issues = new HashSet<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PullRequest> pullRequests = new HashSet<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Commit> commits = new HashSet<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> files = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "repository_contributors",
            joinColumns = @JoinColumn(name = "repository_id"),
            inverseJoinColumns = @JoinColumn(name = "contributor_id")
    )
    private Set<Contributor> contributors = new HashSet<>();
}
