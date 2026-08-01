package com.example.springbackendtemplate1.currency.model.entity;

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

@Getter
@Setter
@Entity
@Table(name = "currency_locales", uniqueConstraints = @UniqueConstraint(columnNames = {"currency_id", "locale_id"}))
public class CurrencyLocaleEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currencyEntity;

    /** Internal — call via {@link CurrencyEntity#addCurrencyLocaleEntity}. */
    public void assignCurrency(CurrencyEntity currencyEntity) {
        this.currencyEntity = currencyEntity;
    }

    /** Internal — call via {@link CurrencyEntity#removeCurrencyLocaleEntity}. */
    public void unassignCurrency() {
        this.currencyEntity = null;
    }

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locale_id", nullable = false)
    private LocaleEntity localeEntity;

    /** Internal — call via {@link LocaleEntity#addCurrencyLocaleEntity}. */
    public void assignLocale(LocaleEntity localeEntity) {
        this.localeEntity = localeEntity;
    }

    /** Internal — call via {@link LocaleEntity#removeCurrencyLocaleEntity}. */
    public void unassignLocale() {
        this.localeEntity = null;
    }

    @NotBlank
    @Size(max = 200)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 100)
    @Column(name = "short_name")
    private String shortName;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
