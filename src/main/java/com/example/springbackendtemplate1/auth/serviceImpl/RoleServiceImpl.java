package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import com.example.springbackendtemplate1.auth.model.mapper.RoleMapper;
import com.example.springbackendtemplate1.auth.repository.RoleRepository;
import com.example.springbackendtemplate1.auth.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public SuccessResponse createRole(CreateRoleRequest request) {
        RoleEntity roleEntity;
        try {
            roleEntity = getRoleEntity(request.getName());
        } catch (EntityNotFoundException ex) {
            log.info("Role not found, creating new role");
            roleEntity = roleRepository.save(RoleMapper.fromRequest(request));
        }
        return new SuccessResponse(true, roleEntity.getId());
    }

    @Override
    public RoleEntity getRoleEntity(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
