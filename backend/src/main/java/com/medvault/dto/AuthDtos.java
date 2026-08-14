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
}