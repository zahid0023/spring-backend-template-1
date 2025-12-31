package com.example.springbackendtemplate1.commons.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    private Long createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private Long updatedBy;

    @LastModifiedDate
    private Instant updatedAt;

    @Version
    private Long version;

    private Boolean isActive = true;
    private Boolean isDeleted = false;

    private Long deletedBy;
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        if (createdBy == null) {
            createdBy = 0L; // SYSTEM
        }

        if (updatedBy == null) {
            updatedBy = createdBy;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (updatedBy == null) {
            updatedBy = createdBy;
        }
    }
}