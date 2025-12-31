package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.dto.request.permission.CreatePermissionRequest;
import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PermissionMapper {
    public static PermissionEntity fromRequest(CreatePermissionRequest request) {
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setName(request.getName());
        permissionEntity.setDescription(request.getDescription());
        return permissionEntity;
    }
}
