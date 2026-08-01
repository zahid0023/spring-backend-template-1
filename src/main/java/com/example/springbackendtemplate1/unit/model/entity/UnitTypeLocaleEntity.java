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
@Table(name = "unit_type_locales", uniqueConstraints = @UniqueConstraint(columnNames = {"unit_type_id", "locale_id"}))
public class UnitTypeLocaleEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_type_id", nullable = false)
    private UnitTypeEntity unitTypeEntity;

    /** Internal — call via {@link UnitTypeEntity#addUnitTypeLocaleEntity}. */
    public void assignUnitType(UnitTypeEntity unitTypeEntity) {
        this.unitTypeEntity = unitTypeEntity;
    }

    /** Internal — call via {@link UnitTypeEntity#removeUnitTypeLocaleEntity}. */
    public void unassignUnitType() {
        this.unitTypeEntity = null;
    }

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "locale_id", nullable = false)
    private LocaleEntity localeEntity;

    /** Internal — call via {@link LocaleEntity#addUnitTypeLocaleEntity}. */
    public void assignLocale(LocaleEntity localeEntity) {
        this.localeEntity = localeEntity;
    }

    /** Internal — call via {@link LocaleEntity#removeUnitTypeLocaleEntity}. */
    public void unassignLocale() {
        this.localeEntity = null;
    }

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description = "";

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
