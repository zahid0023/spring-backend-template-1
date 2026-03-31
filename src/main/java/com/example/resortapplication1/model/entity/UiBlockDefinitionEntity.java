package com.example.resortapplication1.model.entity;

import com.example.resortapplication1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "ui_block_definition")
public class UiBlockDefinitionEntity extends AuditableEntity {

    @Size(max = 100)
    @NotNull
    @Column(name = "ui_block_key", nullable = false, length = 100)
    private String uiBlockKey;

    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'1.0.0'")
    @Column(name = "ui_block_version", nullable = false, length = 20)
    private String uiBlockVersion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ui_block_category_id", nullable = false)
    private UiBlockCategoryEntity uiBlockCategoryEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_type_id", nullable = false)
    private PageTypeEntity pageTypeEntity;

    @NotNull
    @Column(name = "editable_schema", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> editableSchema;

    @NotNull
    @Column(name = "default_content", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> defaultContent;

    @Column(name = "allowed_pages")
    private List<String> allowedPages;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'draft'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;
}