package com.example.springbackendtemplate1.image.hosting.model.entity;

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
@Table(name = "image_hosting_providers")
public class ImageHostingProviderEntity extends AuditableEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description = "";

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "imageHostingProviderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageHostingProviderConfigFieldEntity> configFieldEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "imageHostingProviderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageHostingConfigEntity> imageHostingConfigEntities = new LinkedHashSet<>();

    // -------------------------------------------------------------------------
    // ConfigField relationship helpers
    // -------------------------------------------------------------------------

    public void addImageHostingProviderConfigFieldEntity(ImageHostingProviderConfigFieldEntity entity) {
        addChild(configFieldEntities, entity, ImageHostingProviderConfigFieldEntity::assignImageHostingProvider, this);
    }

    public void removeImageHostingProviderConfigFieldEntity(ImageHostingProviderConfigFieldEntity entity) {
        removeChild(configFieldEntities, entity, (child, ignored) -> child.unassignImageHostingProvider());
    }

    // -------------------------------------------------------------------------
    // ImageHostingConfig relationship helpers
    // -------------------------------------------------------------------------

    public void addImageHostingConfigEntity(ImageHostingConfigEntity entity) {
        addChild(imageHostingConfigEntities, entity, ImageHostingConfigEntity::assignImageHostingProvider, this);
    }

    public void removeImageHostingConfigEntity(ImageHostingConfigEntity entity) {
        removeChild(imageHostingConfigEntities, entity, (child, ignored) -> child.unassignImageHostingProvider());
    }
}
