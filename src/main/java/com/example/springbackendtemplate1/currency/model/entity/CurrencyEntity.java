package com.example.springbackendtemplate1.currency.model.entity;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
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

import java.util.LinkedHashSet;
import java.util.Set;

import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;

@Getter
@Setter
@Entity
@Table(
        name = "currencies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "country_id"})
)
public class CurrencyEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 3)
    @Column(name = "code", nullable = false, unique = true, length = 3)
    private String code;

    @Size(max = 3)
    @Column(name = "numeric_code", unique = true, length = 3)
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

    @OneToMany(mappedBy = "currencyEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CurrencyLocaleEntity> currencyLocaleEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // Currency Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        addChild(currencyLocaleEntities, entity, CurrencyLocaleEntity::assignCurrencyEntity, this);
    }

    public void removeCurrencyLocaleEntity(CurrencyLocaleEntity entity) {
        removeChild(currencyLocaleEntities, entity, (child, ignored) -> child.unassignCurrencyEntity());
    }

}
