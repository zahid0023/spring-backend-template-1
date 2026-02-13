package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.model.enitty.RefreshTokenEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import lombok.experimental.UtilityClass;
import java.time.OffsetDateTime;

@UtilityClass
public class RefreshTokenMapper {

    public static RefreshTokenEntity toEntity(UserEntity user, String refreshToken, Integer refreshTokenExpiryDays) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setToken(refreshToken);
        entity.setExpiresAt(OffsetDateTime.now().plusDays(refreshTokenExpiryDays));
        return entity;
    }
}
