package com.bilkent.devinsight.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "issues")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Issue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Boolean isClosed;

    @NotNull
    private Date createdAt;

    @Nullable
    private Date closedAt;

    @ManyToOne
    @Nullable
    private Contributor closedBy;

    @NotNull
    private Integer severityRating;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

}