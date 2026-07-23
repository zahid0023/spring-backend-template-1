package com.example.springbackendtemplate1.address.model.entity;

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
@Table(name = "countries")
public class CountryEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 10)
    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Size(max = 10)
    @Column(name = "iso3_code", length = 10)
    private String iso3Code;

    @Size(max = 10)
    @Column(name = "phone_code", length = 10)
    private String phoneCode;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "countryEntity", cascade = CascadeType.ALL)
    private Set<CountryLocaleEntity> countryLocaleEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "countryEntity")
    private Set<CityEntity> cityEntities = new LinkedHashSet<>();

}
