package com.example.springbackendtemplate1.auth.dto.response;

import lombok.Data;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VerifyOtpResponse {
    private final String resetToken;

    public VerifyOtpResponse(String resetToken) {
        this.resetToken = resetToken;
    }
}
