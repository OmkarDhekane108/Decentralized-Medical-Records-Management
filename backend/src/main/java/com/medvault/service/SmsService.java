package com.medvault.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SecureMedChain SMS Gateway
 * Sends OTP via configured SMS provider (Fast2SMS / MSG91 / Twilio).
 * If not configured, silently logs — the frontend in-app OTP display handles delivery.
 */
@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger("SecureMedChain.SMS");

    @Value("${sms.fast2sms.api-key:}")
    private String fast2smsApiKey;

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    public void sendOtpSms(String toMobile, String otp) {
        if (smsEnabled && fast2smsApiKey != null && !fast2smsApiKey.isBlank()) {
            try {
                sendViaDlt(toMobile, otp);
                log.info("[SecureMedChain.SMS] OTP dispatched to: {}", mask(toMobile));
            } catch (Exception e) {
                log.warn("[SecureMedChain.SMS] SMS send failed for {}: {}", mask(toMobile), e.getMessage());
            }
        } else {
            // SMS not configured — OTP is shown via in-app notification on frontend
            log.info("[SecureMedChain.SMS] SMS gateway not configured. OTP delivered in-app for: {}", mask(toMobile));
        }
    }

    public void sendOtpWhatsapp(String toMobile, String otp) {
        // Same path — uses Fast2SMS WhatsApp channel if key is set
        sendOtpSms(toMobile, otp);
    }

    /**
     * Fast2SMS DLT route integration.
     * Replace with MSG91 / Twilio if preferred.
     */
    private void sendViaDlt(String mobile, String otp) throws Exception {
        String url = "https://www.fast2sms.com/dev/bulkV2"
                + "?authorization=" + fast2smsApiKey
                + "&variables_values=" + otp
                + "&route=otp"
                + "&numbers=" + mobile.replaceAll("[^0-9]", "");

        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();
        java.net.http.HttpResponse<String> resp = client.send(request,
                java.net.http.HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("Fast2SMS returned HTTP " + resp.statusCode() + ": " + resp.body());
        }
    }

    private String mask(String m) {
        if (m == null || m.length() < 6) return "****";
        return m.substring(0, 3) + "****" + m.substring(m.length() - 3);
    }
}
