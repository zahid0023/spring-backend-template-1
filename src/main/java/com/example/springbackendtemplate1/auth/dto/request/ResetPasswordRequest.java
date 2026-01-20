package com.example.springbackendtemplate1.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResetPasswordRequest {
    private String token;

    @JsonAlias("new_password")
    private String newPassword;
}
