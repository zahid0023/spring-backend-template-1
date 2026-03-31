package com.example.resortapplication1.auth.service;

import com.example.resortapplication1.auth.dto.request.role.CreateRoleRequest;
import com.example.resortapplication1.commons.dto.response.SuccessResponse;
import com.example.resortapplication1.auth.model.enitty.RoleEntity;

public interface RoleService {
    SuccessResponse createRole(CreateRoleRequest request);

    RoleEntity getRoleEntity(String name);
}
