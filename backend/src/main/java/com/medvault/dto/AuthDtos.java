package com.medvault.dto;

public class AuthDtos {

    public static class LoginRequest {
        public String username;
        public String password;
        public String role;
    }

    public static class LoginResponse {
        public String token;
        public String username;
        public String role;
        public String fullName;

        public LoginResponse(String token, String username, String role, String fullName) {
            this.token = token;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
        }
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String fullName;
        public Integer age;
        public Double weight;
        public String mobile;
        public String email;
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }

    // ---------- OTP DTOs ----------

    public static class SendMobileOtpRequest {
        public String mobile;
        public boolean useWhatsapp; // optional: true = send via WhatsApp instead of SMS
    }

    public static class VerifyMobileOtpRequest {
        public String mobile;
        public String otp;
    }

    public static class SendEmailOtpRequest {
        public String email;
    }

    public static class VerifyEmailOtpRequest {
        public String email;
        public String otp;
    }

    public static class SimpleMessageResponse {
        public String message;
        public SimpleMessageResponse(String message) { this.message = message; }
    }

    // ---------- Auto-credential registration response ----------

    public static class RegisterResultResponse {
        public String message;
        public String username;
        public String password; // shown once in the response; also emailed/texted
        public String role;

        public RegisterResultResponse(String message, String username, String password, String role) {
            this.message = message;
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
}
