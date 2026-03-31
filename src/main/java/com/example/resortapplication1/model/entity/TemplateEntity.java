package com.example.resortapplication1.model.entity;

import com.example.resortapplication1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "template")
public class TemplateEntity extends AuditableEntity {

    @Size(max = 100)
    @NotNull
    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'draft'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "templateEntity")
    private Set<TemplatePageEntity> templatePageEntities = new LinkedHashSet<>();

}