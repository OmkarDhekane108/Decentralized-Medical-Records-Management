package com.medvault.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final long OTP_VALID_MINUTES = 5;
    private static final long VERIFIED_FLAG_VALID_MINUTES = 15; // how long "verified" stays valid before /register

    private final SecureRandom random = new SecureRandom();

    // key -> OTP entry (mobile or email as key)
    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // key -> when it was verified (so /register can double check without trusting the client)
    private final ConcurrentHashMap<String, Instant> verifiedStore = new ConcurrentHashMap<>();

    private static class OtpEntry {
        String code;
        Instant expiresAt;
        OtpEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    /** Generates a 6-digit OTP, stores it against the key, and returns it so the caller can send it. */
    public String generateOtp(String key) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(key, new OtpEntry(code, Instant.now().plusSeconds(OTP_VALID_MINUTES * 60)));
        verifiedStore.remove(key); // invalidate any old verified flag when a new OTP is requested
        return code;
    }

    /** Returns true and marks the key as verified if the OTP matches and hasn't expired. */
    public boolean verifyOtp(String key, String submittedOtp) {
        OtpEntry entry = otpStore.get(key);
        if (entry == null) {
            return false; // no OTP was ever requested, or it already expired/was cleared
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            otpStore.remove(key);
            return false;
        }
        if (!entry.code.equals(submittedOtp)) {
            return false; // OTP not matched
        }
        otpStore.remove(key); // one-time use
        verifiedStore.put(key, Instant.now());
        return true;
    }

    /** Used by /register to confirm this mobile/email was actually OTP-verified recently. */
    public boolean isVerified(String key) {
        Instant verifiedAt = verifiedStore.get(key);
        if (verifiedAt == null) return false;
        if (Instant.now().isAfter(verifiedAt.plusSeconds(VERIFIED_FLAG_VALID_MINUTES * 60))) {
            verifiedStore.remove(key);
            return false;
        }
        return true;
    }

    /** Call after successful registration so the same OTP verification can't be reused for another account. */
    public void clearVerified(String key) {
        verifiedStore.remove(key);
    }
}
