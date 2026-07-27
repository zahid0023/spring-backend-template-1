package com.example.springbackendtemplate1.address.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import jakarta.persistence.*;
import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class CountryEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 10)
    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank
    @Size(max = 10)
    @Column(name = "iso3_code", nullable = false, length = 10)
    private String iso3Code;

    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = "^[A-Za-z]{1,3}$")
    @Column(name = "phone_code", nullable = false, length = 10)
    private String phoneCode;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "countryEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CountryLocaleEntity> countryLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "countryEntity")
    private Set<CityEntity> cityEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "countryEntity")
    private Set<CurrencyEntity> currencyEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // Country Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCountryLocaleEntity(CountryLocaleEntity entity) {
        addChild(countryLocaleEntities, entity, CountryLocaleEntity::assignCountry, this);
    }

    public void removeCountryLocaleEntity(CountryLocaleEntity entity) {
        removeChild(countryLocaleEntities, entity, (child, ignored) -> child.unassignCountry());
    }

    // -------------------------------------------------------------------------
    // City relationship helpers
    // -------------------------------------------------------------------------

    public void addCityEntity(CityEntity entity) {
        addChild(cityEntities, entity, CityEntity::assignCountry, this);
    }

    public void removeCityEntity(CityEntity entity) {
        removeChild(cityEntities, entity, (child, ignored) -> child.unassignCountry());
    }

    // -------------------------------------------------------------------------
    // Currency relationship helpers
    // -------------------------------------------------------------------------

    public void addCurrencyEntity(CurrencyEntity entity) {
        addChild(currencyEntities, entity, CurrencyEntity::assignCountry, this);
    }

    public void removeCurrencyEntity(CurrencyEntity entity) {
        removeChild(currencyEntities, entity, (child, ignored) -> child.unassignCountry());
    }

}
