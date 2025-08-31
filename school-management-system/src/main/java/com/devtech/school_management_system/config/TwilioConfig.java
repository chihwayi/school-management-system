package com.devtech.school_management_system.config;

import com.twilio.Twilio;
import com.twilio.http.HttpClient;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    @Bean
    public String initializeTwilio() {
        if (whatsappEnabled && accountSid != null && authToken != null && 
            !accountSid.equals("your-twilio-account-sid") && !authToken.equals("your-twilio-auth-token")) {
            Twilio.init(accountSid, authToken);
            return "Twilio initialized successfully";
        }
        return "Twilio not initialized";
    }

    @Bean
    public TwilioRestClient twilioRestClient() {
        if (whatsappEnabled && accountSid != null && authToken != null && 
            !accountSid.equals("your-twilio-account-sid") && !authToken.equals("your-twilio-auth-token")) {
            return new TwilioRestClient.Builder(accountSid, authToken).build();
        }
        return null;
    }

    public boolean isWhatsAppEnabled() {
        return whatsappEnabled;
    }
}
