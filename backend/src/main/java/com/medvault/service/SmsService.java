package com.medvault.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${twilio.whatsapp-number}")
    private String twilioWhatsappNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendOtpSms(String toMobile, String otp) {
        Message.creator(
                new PhoneNumber(toMobile),
                new PhoneNumber(twilioPhoneNumber),
                "Your MedChain OTP is " + otp + ". Valid for 5 minutes."
        ).create();
    }

    public void sendOtpWhatsapp(String toMobile, String otp) {
        Message.creator(
                new PhoneNumber("whatsapp:" + toMobile),
                new PhoneNumber(twilioWhatsappNumber),
                "Your MedChain OTP is " + otp + ". Valid for 5 minutes."
        ).create();
    }
}
