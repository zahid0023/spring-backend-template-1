package com.example.springbackendtemplate1.auth.model.enitty;

import com.example.resortbackendapplication1.commons.model.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity extends AuditableEntity {
    @Column(name = "name", nullable = false, length = 50)
    private String name;
}