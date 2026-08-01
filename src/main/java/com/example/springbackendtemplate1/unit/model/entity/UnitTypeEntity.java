package com.example.springbackendtemplate1.unit.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

import jakarta.validation.constraints.NotBlank;
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
@Table(name = "unit_types")
public class UnitTypeEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "unitTypeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitTypeLocaleEntity> unitTypeLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "unitTypeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UnitEntity> unitEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // UnitType Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addUnitTypeLocaleEntity(UnitTypeLocaleEntity entity) {
        addChild(unitTypeLocaleEntities, entity, UnitTypeLocaleEntity::assignUnitType, this);
    }

    public void removeUnitTypeLocaleEntity(UnitTypeLocaleEntity entity) {
        removeChild(unitTypeLocaleEntities, entity, (child, ignored) -> child.unassignUnitType());
    }

    // -------------------------------------------------------------------------
    // Unit relationship helpers
    // -------------------------------------------------------------------------

    public void addUnitEntity(UnitEntity entity) {
        addChild(unitEntities, entity, UnitEntity::assignUnitType, this);
    }

    public void removeUnitEntity(UnitEntity entity) {
        removeChild(unitEntities, entity, (child, ignored) -> child.unassignUnitType());
    }
}
