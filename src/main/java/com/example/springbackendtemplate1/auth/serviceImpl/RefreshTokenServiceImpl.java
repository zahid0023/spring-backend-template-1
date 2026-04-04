package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.config.JwtTokenProvider;
import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;
import com.example.springbackendtemplate1.auth.model.enitty.RefreshTokenEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.repository.RefreshTokenRepository;
import com.example.springbackendtemplate1.auth.service.RefreshTokenService;
import com.example.springbackendtemplate1.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Value("${jwt.refresh-token-expiration-days}")
    private Integer refreshTokenExpirationDays;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   JwtTokenProvider jwtTokenProvider,
                                   UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    @Transactional
    public String createRefreshToken(Authentication authentication, UserEntity user) {
        String token = jwtTokenProvider.generateRefreshToken(authentication);

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiresAt(OffsetDateTime.now().plusDays(refreshTokenExpirationDays));
        refreshTokenRepository.save(entity);

        return token;
    }

    @Override
    @Transactional
    public LoginResponse rotateRefreshToken(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository
                .findByTokenAndIsActiveAndIsDeleted(refreshToken, true, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or revoked refresh token"));

        if (tokenEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            tokenEntity.setIsDeleted(true);
            refreshTokenRepository.save(tokenEntity);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        String username = jwtTokenProvider.extractUsername(refreshToken);
        if (username == null || !jwtTokenProvider.isTokenValid(refreshToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // Revoke old token (rotation — one-time use)
        tokenEntity.setIsDeleted(true);
        refreshTokenRepository.save(tokenEntity);

        // Issue new token pair
        UserDetails userDetails = userService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        UserEntity userEntity = userService.getUserByUsername(username);
        String newRefreshToken = createRefreshToken(authentication, userEntity);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UserEntity user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
