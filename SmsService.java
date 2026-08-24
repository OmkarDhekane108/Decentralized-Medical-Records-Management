@'
package com.medvault.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendOtpSms(String toMobile, String otp) {
        System.out.println("========================================");
        System.out.println("[DEMO SMS] To: " + toMobile);
        System.out.println("[DEMO SMS] Message: " + otp);
        System.out.println("========================================");
    }

    public void sendOtpWhatsapp(String toMobile, String otp) {
        sendOtpSms(toMobile, otp);
    }
}
'@ | Set-Content -Path src\main\java\com\medvault\service\SmsService.java -Encoding UTF8