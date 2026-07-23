package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleMapper {
    public static RoleEntity fromRequest(CreateRoleRequest request) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(request.getName());
        return roleEntity;
    }
}
