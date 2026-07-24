package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface UserRepository extends
        JpaRepository<@NonNull UserEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UserEntity> {

    Optional<UserEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UserEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UserEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UserEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UserEntity> findAllByRoleEntityAndIsActiveAndIsDeleted(
            RoleEntity roleEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);

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
                left join fetch up.permissionEntity
                where u.username = :username
            """)
    Optional<UserEntity> findByUsernameWithAuthorities(String username);

}
