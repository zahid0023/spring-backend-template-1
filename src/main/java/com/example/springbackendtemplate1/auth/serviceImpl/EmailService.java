package com.example.springbackendtemplate1.auth.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtp(String toEmail, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(toEmail);
        helper.setSubject("Password Reset Request");

        ZonedDateTime now = ZonedDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a z"));

        String emailContent = String.format(
            "Hello,\n\n" +
            "Someone is attempting to reset the password of your account.\n\n" +
            "When: %s\n" +
            "If this was you, your verification code is:\n\n" +
            "%s\n\n" +
            "If you didn't request it: Contact Us.\n\n" +
            "Don’t share it with others.",
            formattedDate, otp
        );

        helper.setText(emailContent);

        javaMailSender.send(mimeMessage);
    }
}
