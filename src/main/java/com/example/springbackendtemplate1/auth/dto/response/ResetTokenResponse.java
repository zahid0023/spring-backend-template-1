package com.example.springbackendtemplate1.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetTokenResponse {
    @JsonAlias("reset_token")
    private String resetToken;
}