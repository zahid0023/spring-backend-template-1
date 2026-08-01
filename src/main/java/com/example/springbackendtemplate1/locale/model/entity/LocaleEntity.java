package com.example.springbackendtemplate1.locale.model.entity;

import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

@Getter
@Setter
@Entity
@Table(name = "locales")
public class LocaleEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "localeEntity")
    private Set<CountryLocaleEntity> countryLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "localeEntity")
    private Set<CityLocaleEntity> cityLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "localeEntity")
    private Set<CurrencyLocaleEntity> currencyLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "localeEntity")
    private Set<UnitTypeLocaleEntity> unitTypeLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "localeEntity")
    private Set<UnitLocaleEntity> unitLocaleEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // Country Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCountryLocaleEntity(CountryLocaleEntity entity) {
        addChild(countryLocaleEntities, entity, CountryLocaleEntity::assignLocale, this);
    }

    public void removeCountryLocaleEntity(CountryLocaleEntity entity) {
        removeChild(countryLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

    // -------------------------------------------------------------------------
    // City Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCityLocaleEntity(CityLocaleEntity entity) {
        addChild(cityLocaleEntities, entity, CityLocaleEntity::assignLocale, this);
    }

    public void removeCityLocaleEntity(CityLocaleEntity entity) {
        removeChild(cityLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

    // -------------------------------------------------------------------------
    // Currency Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        addChild(currencyLocaleEntities, entity, CurrencyLocaleEntity::assignLocale, this);
    }

    public void removeCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        removeChild(currencyLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

    // -------------------------------------------------------------------------
    // UnitType Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addUnitTypeLocaleEntity(UnitTypeLocaleEntity entity) {
        addChild(unitTypeLocaleEntities, entity, UnitTypeLocaleEntity::assignLocale, this);
    }

    public void removeUnitTypeLocaleEntity(UnitTypeLocaleEntity entity) {
        removeChild(unitTypeLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

    // -------------------------------------------------------------------------
    // Unit Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addUnitLocaleEntity(UnitLocaleEntity entity) {
        addChild(unitLocaleEntities, entity, UnitLocaleEntity::assignLocale, this);
    }

    public void removeUnitLocaleEntity(UnitLocaleEntity entity) {
        removeChild(unitLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

}
