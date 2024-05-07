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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contributors")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Contributor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String url;

    @NotNull
    private String avatarUrl;

    @NotNull
    private String name;

    @NotNull
    private String email;

}
