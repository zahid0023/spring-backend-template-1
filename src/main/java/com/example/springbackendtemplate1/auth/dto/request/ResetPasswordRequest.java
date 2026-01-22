package com.example.springbackendtemplate1.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResetPasswordRequest {
    @JsonAlias("reset_token")
    private String resetToken;

    @JsonAlias("new_password")
    private String newPassword;

    @JsonAlias("confirm_password")
    private String confirmPassword;
}
