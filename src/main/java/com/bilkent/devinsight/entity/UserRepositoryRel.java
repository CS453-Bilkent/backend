package com.bilkent.devinsight.entity;


import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_repository")
@EntityListeners(AuditingEntityListener.class)
public class UserRepositoryRel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Repository repository;


}
