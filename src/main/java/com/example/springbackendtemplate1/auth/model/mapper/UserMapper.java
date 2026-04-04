package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.dto.request.RegistrationRequest;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserMapper {
    public static UserEntity fromRequest(RegistrationRequest request,
                                         RoleEntity roleEntity,
                                         PasswordEncoder passwordEncoder) {
        UserEntity userEntity = new UserEntity();
        userEntity.setRoleEntity(roleEntity);
        userEntity.setUsername(request.getUserName());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        return userEntity;
    }

    public static UserEntity toUserEntity(
            String username,
            String password,
            RoleEntity roleEntity) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setRoleEntity(roleEntity);
        userEntity.setEnabled(true);
        userEntity.setLocked(false);
        userEntity.setExpired(false);
        return userEntity;
    }
}
