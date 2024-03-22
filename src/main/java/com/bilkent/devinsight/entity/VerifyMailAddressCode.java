package com.bilkent.devinsight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Table(name = "verify_mail_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyMailAddressCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Date expireDate;

    @NotNull
    private String code;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @NotNull
    private boolean used = false;

    @NotNull
    private boolean valid = true;

}
