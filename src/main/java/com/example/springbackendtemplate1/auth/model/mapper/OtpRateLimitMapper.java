package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.model.enitty.OtpRateLimitEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;

@UtilityClass
public class OtpRateLimitMapper {
    public static OtpRateLimitEntity toRateLimitEntity(UserEntity userEntity, OffsetDateTime windowStart) {
        return OtpRateLimitEntity.builder()
                .user(userEntity)
                .windowStart(windowStart)
                .requestCount(0)
                .build();
    }
}
