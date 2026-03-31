package com.example.resortapplication1.model.entity;

import com.example.resortapplication1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "template_page")
public class TemplatePageEntity extends AuditableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private TemplateEntity templateEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_type_id", nullable = false)
    private PageTypeEntity pageTypeEntity;

    @Size(max = 150)
    @NotNull
    @Column(name = "page_name", nullable = false, length = 150)
    private String pageName;

    @Size(max = 150)
    @NotNull
    @Column(name = "page_slug", nullable = false, length = 150)
    private String pageSlug;

    @NotNull
    @Column(name = "page_order", nullable = false)
    private Integer pageOrder;

    @OneToMany(mappedBy = "templatePageEntity")
    private Set<TemplatePageSlotEntity> templatePageSlotEntities = new LinkedHashSet<>();

}