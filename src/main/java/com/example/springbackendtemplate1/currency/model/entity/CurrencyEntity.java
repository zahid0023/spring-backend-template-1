package com.example.springbackendtemplate1.currency.model.entity;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
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
@Table(name = "currencies")
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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity countryEntity;

    @OneToMany(mappedBy = "currencyEntity", cascade = CascadeType.ALL)
    private Set<CurrencyLocaleEntity> currencyLocaleEntities = new LinkedHashSet<>();
}
