package com.example.springbackendtemplate1.commons.config;

import com.example.springbackendtemplate1.auth.model.dto.CustomUserDetails;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<@NonNull Long> {

    @Override
    public @NonNull Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        // Assume your UserDetails has the user ID
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        assert userDetails != null;
        return Optional.of(userDetails.getId());
    }
}
