package com.example.springbackendtemplate1.auth.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate6DigitOtp() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }
}
