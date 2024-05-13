package com.bilkent.devinsight.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pull_requests")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PullRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private int pullRequestId;

    @NotNull
    private String url;

    @NotNull
    private String title;

    @NotNull
    private Date createdAt;

    @Nullable
    private Date mergedAt;

    @NotNull
    private Integer numberOfComments;

    @NotNull
    private Boolean reviewed;

    @NotNull
    private Integer size;

    @NotNull
    private Integer additions;

    @NotNull
    private Integer deletions;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "pull_requests_requested_reviewers",
            joinColumns = @JoinColumn(name = "pull_requests_id"),
            inverseJoinColumns = @JoinColumn(name = "contributor_id"))
    private Set<Contributor> requestedReviewers = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Contributor> assignees = new HashSet<>();

}
