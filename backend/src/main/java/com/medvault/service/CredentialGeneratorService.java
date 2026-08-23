package com.medvault.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CredentialGeneratorService {

    private static final String PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";

    private final SecureRandom random = new SecureRandom();

    /** e.g. "ravi.kumar" + "email" -> "ravikumar4821" */
    public String generateUsername(String fullName) {
        String base = (fullName == null || fullName.isBlank() ? "user" : fullName)
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
        if (base.isBlank()) base = "user";
        int suffix = 1000 + random.nextInt(9000);
        return base + suffix;
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
