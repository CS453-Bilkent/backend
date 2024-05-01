package com.bilkent.devinsight.entity;


import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commits")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Commit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Date timestamp;

    @NotNull
    private String hash;

    @NotNull
    private Date commitTime;

    @ManyToOne
    private Contributor contributor;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ManyToMany
    private Set<File> changedFiles = new HashSet<>();
}