package com.example.springbackendtemplate1.unit.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(
        name = "unit_locales",
        uniqueConstraints = @UniqueConstraint(columnNames = {"unit_id", "locale_id"})
)
public class UnitLocaleEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitEntity unitEntity;

    public void assignUnitEntity(UnitEntity unitEntity) {
        this.unitEntity = unitEntity;
    }

    public void unassignUnitEntity() {
        this.unitEntity = null;
    }

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "locale_id", nullable = false)
    private LocaleEntity localeEntity;

    public void assignLocaleEntity(LocaleEntity localeEntity) {
        this.localeEntity = localeEntity;
    }

    public void unassignLocaleEntity() {
        this.localeEntity = null;
    }

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "plural_name", nullable = false, length = 100)
    private String pluralName;

    @ColumnDefault("''")
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description = "";

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

}
