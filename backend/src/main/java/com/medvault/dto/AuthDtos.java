package com.medvault.dto;

public class AuthDtos {

    public static class LoginRequest {
        public String username;
        public String password;
        public String role; // Patient | Doctor | Admin — matches the login.html dropdown
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

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
