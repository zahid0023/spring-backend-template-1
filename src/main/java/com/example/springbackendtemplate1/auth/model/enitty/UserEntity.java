package com.example.springbackendtemplate1.auth.model.enitty;

import com.example.springbackendtemplate1.commons.model.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity extends AuditableEntity {
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity roleEntity;

    @ColumnDefault("true")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @ColumnDefault("false")
    @Column(name = "locked", nullable = false)
    private Boolean locked = true;

    @OneToMany(mappedBy = "user")
    private Set<RefreshTokenEntity> refreshTokens = new LinkedHashSet<>();

    @NotNull
    @ColumnDefault("false")
    @Column(name = "expired", nullable = false)
    private Boolean expired = false;

    @OneToMany(mappedBy = "user")
    private Set<UserPermissionEntity> userPermissions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userEntity")
    private Set<PasswordResetOtpEntity> passwordResetOtps = new LinkedHashSet<>();

}