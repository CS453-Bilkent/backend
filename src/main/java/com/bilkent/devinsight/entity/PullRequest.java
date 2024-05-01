package com.bilkent.devinsight.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pull_requests")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PullRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime mergedAt;

    @NotNull
    private Integer numberOfComments;

    @NotNull
    private Boolean reviewed;

    @NotNull
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ManyToMany
    private Set<Contributor> contributors = new HashSet<>();
}
