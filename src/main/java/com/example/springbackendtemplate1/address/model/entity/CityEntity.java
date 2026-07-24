package com.example.springbackendtemplate1.address.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

@Getter
@Setter
@Entity
@Table(name = "cities")
public class CityEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity countryEntity;

    public void assignCountryEntity(CountryEntity countryEntity) {
        this.countryEntity = countryEntity;
    }

    public void unassignCountryEntity() {
        this.countryEntity = null;
    }

    @Size(max = 50)
    @Column(name = "code", length = 50)
    private String code;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "cityEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CityLocaleEntity> cityLocaleEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // City Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCityLocaleEntity(CityLocaleEntity entity) {
        addChild(cityLocaleEntities, entity, CityLocaleEntity::assignCityEntity, this);
    }

    public void removeCityLocaleEntity(CityLocaleEntity entity) {
        removeChild(cityLocaleEntities, entity, (child, ignored) -> child.unassignCityEntity());
    }

}
