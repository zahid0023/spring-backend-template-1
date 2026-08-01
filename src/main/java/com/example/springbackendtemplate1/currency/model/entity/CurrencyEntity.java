package com.example.springbackendtemplate1.currency.model.entity;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "currencies")
public class CurrencyEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity countryEntity;

    /** Internal — call via {@link CountryEntity#addCurrencyEntity}. */
    public void assignCountry(CountryEntity countryEntity) {
        this.countryEntity = countryEntity;
    }

    /** Internal — call via {@link CountryEntity#removeCurrencyEntity}. */
    public void unassignCountry() {
        this.countryEntity = null;
    }

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[A-Z]{3}$")
    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;

    @NotBlank
    @Size(max = 3)
    @Pattern(regexp = "^[0-9]{3}$")
    @Column(name = "numeric_code", nullable = false, unique = true, length = 3)
    private String numericCode;

    @NotBlank
    @Size(max = 10)
    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @NotNull
    @ColumnDefault("2")
    @Column(name = "decimal_places", nullable = false)
    private Integer decimalPlaces = 2;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "currencyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CurrencyLocaleEntity> currencyLocaleEntities = new LinkedHashSet<>();

    public void addCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        addChild(currencyLocaleEntities, entity, CurrencyLocaleEntity::assignCurrency, this);
    }

    public void removeCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        removeChild(currencyLocaleEntities, entity, (child, ignored) -> child.unassignCurrency());
    }
}
