package com.example.springbackendtemplate1.auth.model.dto;

import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@EqualsAndHashCode(of = "id")
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final boolean accountNonExpired;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity userEntity) {
        Objects.requireNonNull(userEntity);
        this.id = userEntity.getId();
        this.username = Objects.requireNonNull(userEntity.getUsername());
        this.password = userEntity.getPassword();
        this.enabled = Boolean.TRUE.equals(userEntity.getEnabled());
        this.accountNonLocked = !Boolean.TRUE.equals(userEntity.getLocked());
        this.accountNonExpired = !Boolean.TRUE.equals(userEntity.getExpired());

        Set<GrantedAuthority> auths = new HashSet<>();

        // Role
        auths.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + userEntity.getRoleEntity().getName()
                )
        );

        // Permissions (must already be fetched)
        userEntity.getUserPermissions().forEach(p ->
                auths.add(new SimpleGrantedAuthority(p.getPermission().getName()))
        );

        this.authorities = Collections.unmodifiableSet(auths);
    }
}