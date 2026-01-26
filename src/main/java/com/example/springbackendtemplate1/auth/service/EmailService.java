package com.example.springbackendtemplate1.auth.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendOtp(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException;
}
