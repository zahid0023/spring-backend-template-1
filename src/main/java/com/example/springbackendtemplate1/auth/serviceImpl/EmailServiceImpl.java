package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String senderEmail;
    private final String senderName;
    private static final String SUBJECT = "Password Reset Request";
    private static final String HTML_BODY_STYLE = "font-family: Arial, sans-serif; color: #333; line-height: 1.5;";
    private static final String OTP_STYLE = "background-color: #f0f0f0; color: #000; padding: 10px 15px; "
            + "display: inline-block; font-weight: bold; font-size: 16px; border-radius: 5px;";

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            @Value("${MAIL_USERNAME}") String senderEmail,
                            @Value("${SENDER_NAME}") String senderName) {
        this.javaMailSender = javaMailSender;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendOtp(String toEmail, String otp) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(senderEmail, senderName);
        helper.setTo(InternetAddress.parse(toEmail));
        helper.setSubject("Password Reset Request");
        helper.setText(otpEmailContent(otp), true);

        javaMailSender.send(mimeMessage);
    }

    /**
     * the HTML content for OTP email
     */
    private String otpEmailContent(String otp) {
        String requestTime = ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a z"));

        return """
                <html>
                <body style="%s">
                    <p>Dear User,</p>
                    <p>We received a request to reset the password associated with your account.</p>
                    <p><strong>Request Time:</strong> %s</p>
                    <p>Your One-Time Verification Code (OTP) is:</p>
                    <p style="%s">%s</p>
                    <p>Please use this code to proceed with resetting your password. This code is valid for a limited time and should be kept confidential.</p>
                    <p>If you did not initiate this request, please ignore this email or contact our support team immediately.</p>
                    <p>Regards,<br/>The [Your Company Name] Security Team</p>
                </body>
                </html>
                """.formatted(HTML_BODY_STYLE, requestTime, OTP_STYLE, otp);
    }
}
