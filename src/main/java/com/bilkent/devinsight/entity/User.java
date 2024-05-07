package com.bilkent.devinsight.entity;

import com.bilkent.devinsight.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @NotNull
    private String picture;

    @NotNull
    private Boolean emailVerified = false;

    @ManyToMany
    private Set<UserRepositoryRel> repositories;

    public String toString(){
        return "User [id=" + id + ", username=" + username + ", name=" + name + ", email=" + email + ", role=" + role + "]";
    }

}
