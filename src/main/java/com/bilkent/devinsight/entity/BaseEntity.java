package com.bilkent.devinsight.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.Lock;

import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;

    @Version
    private Long version;

}