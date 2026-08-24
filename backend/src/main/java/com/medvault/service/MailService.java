package com.medvault.service;

import org.springframework.stereotype.Service;

@Service
public class MailService {

    public void sendOtpEmail(String toEmail, String otp) {
        System.out.println("========================================");
        System.out.println("[DEMO EMAIL] To: " + toEmail);
        System.out.println("[DEMO EMAIL] OTP: " + otp);
        System.out.println("========================================");
    }

    public void sendCredentialsEmail(String toEmail, String username, String password) {
        System.out.println("========================================");
        System.out.println("[DEMO EMAIL] To: " + toEmail);
        System.out.println("[DEMO EMAIL] Username: " + username);
        System.out.println("[DEMO EMAIL] Password: " + password);
        System.out.println("========================================");
    }
}