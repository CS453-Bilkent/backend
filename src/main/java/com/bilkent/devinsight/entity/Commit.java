package com.bilkent.devinsight.entity;


import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime timestamp;

    @NotNull
    private String hash;

    @NotNull
    private LocalDateTime commitTime;

    @ManyToOne
    private Contributor contributor;

    @ManyToMany
    private Set<File> changedFiles = new HashSet<>();
}