package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;

public interface RoleService {
    SuccessResponse createRole(CreateRoleRequest request);

    RoleEntity getRoleEntity(String name);
}
