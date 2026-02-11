package com.example.springbackendtemplate1.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @JsonAlias("user_name")
    private String userName;
    private String otp;
}