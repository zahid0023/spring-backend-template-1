package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.RegistrationRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.dto.CustomUserDetails;
import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.model.mapper.UserMapper;
import com.example.springbackendtemplate1.auth.repository.RoleRepository;
import com.example.springbackendtemplate1.auth.repository.UserRepository;
import com.example.springbackendtemplate1.auth.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SuccessResponse registerAdmin(RegistrationRequest request) {
        UserEntity userEntity = registerWithRole(request, "ADMIN");
        return new SuccessResponse(true, userEntity.getId());
    }

    @Override
    // Regular user registration
    public SuccessResponse registerUser(RegistrationRequest request) {
        UserEntity userEntity = registerWithRole(request, "USER");
        return new SuccessResponse(true, userEntity.getId());
    }

    /**
     * Common registration method for any role
     */
    private UserEntity registerWithRole(RegistrationRequest request, String roleName) {
        RoleEntity roleEntity = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found"));

        UserEntity userEntity = UserMapper.fromRequest(request, roleEntity, passwordEncoder);

        return userRepository.save(userEntity);
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findByUsernameWithAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new CustomUserDetails(userEntity);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    @Override
    public SuccessResponse activateAdmin(UserEntity userEntity) {
        userEntity.setEnabled(true);
        userEntity.setLocked(false);
        userEntity.setExpired(false);
        userRepository.save(userEntity);
        return new SuccessResponse(true, userEntity.getId());
    }

    @Override
    public Boolean existsSuperAdmin(String username) {
        try {
            getUserByUsername(username);
            return true;
        } catch (EntityNotFoundException ex) {
            return false;
        }
    }

    @Override
    public UserEntity getAuthenticatedUserEntity() {
        CustomUserDetails customUserDetails = (CustomUserDetails) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert customUserDetails != null;
        return getUserById(customUserDetails.getId());
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
