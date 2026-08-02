package com.example.springbackendtemplate1.image.hosting.model.entity;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "image_hosting_provider_configs")
public class ImageHostingProviderConfigEntity extends AuditableEntity {

    @Setter(AccessLevel.NONE)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_hosting_provider_id", nullable = false)
    private ImageHostingProviderEntity imageHostingProviderEntity;

    /** Internal — call via {@link ImageHostingProviderEntity#addImageHostingProviderConfigEntity}. */
    public void assignImageHostingProvider(ImageHostingProviderEntity imageHostingProviderEntity) {
        this.imageHostingProviderEntity = imageHostingProviderEntity;
    }

    /** Internal — call via {@link ImageHostingProviderEntity#removeImageHostingProviderConfigEntity}. */
    public void unassignImageHostingProvider() {
        this.imageHostingProviderEntity = null;
    }

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> config;
}
