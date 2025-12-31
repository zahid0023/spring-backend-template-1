package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;

public interface RoleService {
    SuccessResponse createRole(CreateRoleRequest request);

    RoleEntity getRoleEntity(String name);
}
