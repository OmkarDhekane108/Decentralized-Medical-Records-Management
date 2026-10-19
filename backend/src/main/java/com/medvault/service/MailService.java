package com.medvault.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger("SecureMedChain.Mail");

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@securemedchain.io}")
    private String fromAddress;

    @Value("${spring.mail.enabled:false}")
    private boolean mailEnabled;

    public void sendOtpEmail(String toEmail, String otp) {
        if (mailEnabled && mailSender != null) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(fromAddress);
                msg.setTo(toEmail);
                msg.setSubject("SecureMedChain — Your OTP Code");
                msg.setText(
                    "Hello,\n\n" +
                    "Your SecureMedChain verification OTP is:\n\n" +
                    "   " + otp + "\n\n" +
                    "This OTP is valid for 5 minutes. Do not share it with anyone.\n\n" +
                    "— SecureMedChain Security Team"
                );
                mailSender.send(msg);
                log.info("[SecureMedChain.Mail] OTP email dispatched to: {}", mask(toEmail));
            } catch (Exception e) {
                log.warn("[SecureMedChain.Mail] Could not send email to {}: {}", mask(toEmail), e.getMessage());
            }
        } else {
            // Mail not configured — OTP is handled by frontend in-app display
            log.info("[SecureMedChain.Mail] Mail service not configured. OTP delivered via in-app notification for: {}", mask(toEmail));
        }
    }

    public void sendCredentialsEmail(String toEmail, String username, String password) {
        if (mailEnabled && mailSender != null) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(fromAddress);
                msg.setTo(toEmail);
                msg.setSubject("SecureMedChain — Registration Successful");
                msg.setText(
                    "Welcome to SecureMedChain!\n\n" +
                    "Your account has been activated. Here are your login credentials:\n\n" +
                    "   Username : " + username + "\n" +
                    "   Password : " + password + "\n\n" +
                    "Please change your password after first login.\n" +
                    "Login at: http://localhost:5501/login.html\n\n" +
                    "— SecureMedChain Team"
                );
                mailSender.send(msg);
                log.info("[SecureMedChain.Mail] Credentials email dispatched to: {}", mask(toEmail));
            } catch (Exception e) {
                log.warn("[SecureMedChain.Mail] Could not send credentials email to {}: {}", mask(toEmail), e.getMessage());
            }
        } else {
            log.info("[SecureMedChain.Mail] Credentials email skipped (mail not configured). Username={}", username);
        }
    }

    private String mask(String s) {
        if (s == null || s.length() < 4) return "****";
        int at = s.indexOf('@');
        if (at > 1) return s.substring(0, 2) + "****" + s.substring(at);
        return s.substring(0, 2) + "****";
    }
}