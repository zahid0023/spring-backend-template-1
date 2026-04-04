package com.example.springbackendtemplate1.auth.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetOtpEvent {
    private final String email;
    private final String otp;
}
