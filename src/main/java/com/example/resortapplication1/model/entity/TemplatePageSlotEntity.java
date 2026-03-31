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
@Table(name = "template_page_slot")
public class TemplatePageSlotEntity extends AuditableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_page_id", nullable = false)
    private TemplatePageEntity templatePageEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ui_block_category_id", nullable = false)
    private UiBlockCategoryEntity uiBlockCategoryEntity;

    @Size(max = 150)
    @NotNull
    @Column(name = "slot_name", nullable = false, length = 150)
    private String slotName;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @NotNull
    @Column(name = "slot_order", nullable = false)
    private Integer slotOrder;

    @OneToMany(mappedBy = "templatePageSlotEntity")
    private Set<TemplatePageSlotVariantEntity> templatePageSlotVariantEntities = new LinkedHashSet<>();

}