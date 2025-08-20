package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.entity.Student;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class WhatsAppService {
    
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);
    
    // Zimbabwe phone number pattern: +263 7X XXX XXXX or 07X XXX XXXX
    private static final Pattern ZIMBABWE_PHONE_PATTERN = 
        Pattern.compile("^(\\+263|263)?[7][1-8][0-9]{7}$");
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.whatsapp.from:}")
    private String fromNumber;
    
    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    public void sendAbsenteeNotification(Student student, List<Guardian> guardians) {
        if (!twilioEnabled) {
            logger.info("Twilio WhatsApp notifications are disabled");
            return;
        }
        
        String message = String.format(
                """
                        Dear Parent/Guardian,
                        
                        We would like to inform you that your child %s %s (ID: %s) was marked absent from school today.
                        
                        If this is an error or if you have any concerns, please contact the school immediately.
                        
                        Thank you,
                        School Administration""",
                student.getFirstName(),
                student.getLastName(),
                student.getStudentId()
        );

        for (Guardian guardian : guardians) {
            if (guardian.getWhatsappNumber() != null && !guardian.getWhatsappNumber().isEmpty()) {
                if (isValidZimbabwePhoneNumber(guardian.getWhatsappNumber())) {
                    sendWhatsAppMessage(guardian.getWhatsappNumber(), message);
                } else {
                    logger.warn("Invalid Zimbabwe phone number for guardian {}: {}", 
                        guardian.getName(), guardian.getWhatsappNumber());
                }
            }
        }
    }

    public void sendMessage(String phoneNumber, String message) {
        if (!twilioEnabled) {
            logger.info("Twilio WhatsApp notifications are disabled");
            return;
        }
        
        if (isValidZimbabwePhoneNumber(phoneNumber)) {
            sendWhatsAppMessage(phoneNumber, message);
        } else {
            logger.warn("Invalid Zimbabwe phone number: {}", phoneNumber);
        }
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        try {
            // Initialize Twilio if not already done
            if (accountSid != null && !accountSid.isEmpty() && 
                authToken != null && !authToken.isEmpty()) {
                Twilio.init(accountSid, authToken);
            } else {
                logger.error("Twilio credentials not configured");
                return;
            }
            
            // Format phone number for WhatsApp
            String formattedNumber = formatPhoneNumberForWhatsApp(phoneNumber);
            
            // Send WhatsApp message
            Message twilioMessage = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedNumber),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    message
                ).create();
            
            logger.info("WhatsApp message sent successfully. SID: {}", twilioMessage.getSid());
            
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }
    
    /**
     * Validate Zimbabwe phone number format
     */
    public boolean isValidZimbabwePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove spaces and dashes
        String cleanedNumber = phoneNumber.replaceAll("[\\s\\-]", "");
        
        return ZIMBABWE_PHONE_PATTERN.matcher(cleanedNumber).matches();
    }
    
    /**
     * Format phone number for WhatsApp API
     */
    private String formatPhoneNumberForWhatsApp(String phoneNumber) {
        // Remove spaces and dashes
        String cleanedNumber = phoneNumber.replaceAll("[\\s\\-]", "");
        
        // If it starts with 0, replace with +263
        if (cleanedNumber.startsWith("0")) {
            cleanedNumber = "+263" + cleanedNumber.substring(1);
        }
        // If it starts with 263, add +
        else if (cleanedNumber.startsWith("263")) {
            cleanedNumber = "+" + cleanedNumber;
        }
        // If it doesn't start with +, add +
        else if (!cleanedNumber.startsWith("+")) {
            cleanedNumber = "+" + cleanedNumber;
        }
        
        return cleanedNumber;
    }
    
    /**
     * Get formatted phone number for display
     */
    public String formatPhoneNumberForDisplay(String phoneNumber) {
        if (!isValidZimbabwePhoneNumber(phoneNumber)) {
            return phoneNumber; // Return as-is if invalid
        }
        
        String cleanedNumber = phoneNumber.replaceAll("[\\s\\-]", "");
        
        // Format as +263 7X XXX XXXX
        if (cleanedNumber.startsWith("0")) {
            cleanedNumber = "+263" + cleanedNumber.substring(1);
        } else if (cleanedNumber.startsWith("263")) {
            cleanedNumber = "+" + cleanedNumber;
        } else if (!cleanedNumber.startsWith("+")) {
            cleanedNumber = "+" + cleanedNumber;
        }
        
        // Add spaces for readability
        if (cleanedNumber.length() == 13) { // +263 7X XXX XXXX
            return cleanedNumber.substring(0, 5) + " " + 
                   cleanedNumber.substring(5, 7) + " " + 
                   cleanedNumber.substring(7, 10) + " " + 
                   cleanedNumber.substring(10);
        }
        
        return cleanedNumber;
    }
}
