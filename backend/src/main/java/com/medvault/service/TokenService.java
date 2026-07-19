package com.medvault.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal token-based session store. Good enough for a small deployed demo;
 * swap for JWT + refresh tokens if this needs to scale past a single instance.
 */
@Service
public class TokenService {

    public record Session(String username, String role) {}

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String issueToken(String username, String role) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(username, role));
        return token;
    }

    public Session validate(String token) {
        if (token == null) return null;
        String cleaned = token.replace("Bearer ", "").trim();
        return sessions.get(cleaned);
    }

    public void revoke(String token) {
        if (token == null) return;
        sessions.remove(token.replace("Bearer ", "").trim());
    }
}
