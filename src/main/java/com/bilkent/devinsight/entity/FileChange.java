package com.bilkent.devinsight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_changes")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FileChange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private Integer additions;

    @NotNull
    private Integer changes;

    @NotNull
    private Integer deletions;

    @NotNull
    private String patch;

    @NotNull
    private String status;

    @NotNull
    private String contentsUrl;

    @NotNull
    private String blobUrl;

    @NotNull
    private String rawUrl;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;
}
