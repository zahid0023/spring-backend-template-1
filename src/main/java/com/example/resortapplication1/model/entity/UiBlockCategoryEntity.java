package com.example.resortapplication1.model.entity;

import com.example.resortapplication1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ui_block_categories")
public class UiBlockCategoryEntity extends AuditableEntity {
    @Size(max = 100)
    @NotNull
    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
}