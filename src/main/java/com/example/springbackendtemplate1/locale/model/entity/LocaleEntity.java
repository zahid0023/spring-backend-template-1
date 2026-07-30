package com.example.springbackendtemplate1.locale.model.entity;

import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
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

    // -------------------------------------------------------------------------
    // Country Locale relationship helpers
    // -------------------------------------------------------------------------

    public void addCountryLocaleEntity(CountryLocaleEntity entity) {
        addChild(countryLocaleEntities, entity, CountryLocaleEntity::assignLocale, this);
    }

    public void removeCountryLocaleEntity(CountryLocaleEntity entity) {
        removeChild(countryLocaleEntities, entity, (child, ignored) -> child.unassignLocale());
    }

}
