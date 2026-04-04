package com.example.springbackendtemplate1.auth.listener;

import com.example.springbackendtemplate1.auth.event.PasswordResetOtpEvent;
import com.example.springbackendtemplate1.auth.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class PasswordResetOtpEmailListener {

    private final EmailService emailService;

    @EventListener
    public void handle(PasswordResetOtpEvent event) throws MessagingException, UnsupportedEncodingException {
        emailService.sendOtpEmail(event.getEmail(), event.getOtp());
    }
}
