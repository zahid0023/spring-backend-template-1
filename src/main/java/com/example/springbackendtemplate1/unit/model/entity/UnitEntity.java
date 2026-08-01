package com.example.springbackendtemplate1.unit.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "units")
public class UnitEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "unit_type_id", nullable = false)
    private UnitTypeEntity unitTypeEntity;

    /** Internal — call via {@link UnitTypeEntity#addUnitEntity}. */
    public void assignUnitType(UnitTypeEntity unitTypeEntity) {
        this.unitTypeEntity = unitTypeEntity;
    }

    /** Internal — call via {@link UnitTypeEntity#removeUnitEntity}. */
    public void unassignUnitType() {
        this.unitTypeEntity = null;
    }

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank
    @Size(max = 20)
    @Column(name = "symbol", nullable = false, unique = true, length = 20)
    private String symbol;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_base_unit", nullable = false)
    private Boolean isBaseUnit = false;

    @NotNull
    @Positive
    @ColumnDefault("1")
    @Column(name = "conversion_factor", nullable = false, precision = 20, scale = 8)
    private BigDecimal conversionFactor = BigDecimal.ONE;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "unitEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitLocaleEntity> unitLocaleEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // Unit Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addUnitLocaleEntity(UnitLocaleEntity entity) {
        addChild(unitLocaleEntities, entity, UnitLocaleEntity::assignUnit, this);
    }

    public void removeUnitLocaleEntity(UnitLocaleEntity entity) {
        removeChild(unitLocaleEntities, entity, (child, ignored) -> child.unassignUnit());
    }
}
