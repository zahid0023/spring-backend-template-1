package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("""
                select u
                from UserEntity u
                join fetch u.roleEntity
                where u.username = :username
            """)
    Optional<UserEntity> findByUsernameWithRole(@Param("username") String username);

    @Query("""
                select distinct u from UserEntity u
                join fetch u.roleEntity
                left join fetch u.userPermissions up
                left join fetch up.permission
                where u.username = :username
            """)
    Optional<UserEntity> findByUsernameWithAuthorities(String username);

    Boolean existsByRoleEntity_Name(String roleName);
}
