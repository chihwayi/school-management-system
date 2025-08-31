package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @Value("${twilio.whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    @Value("${twilio.whatsapp.retry.attempts:3}")
    private int retryAttempts;

    @Value("${twilio.whatsapp.retry.delay:5000}")
    private int retryDelay;

    @Autowired
    private TwilioConfig twilioConfig;

    /**
     * Send attendance notification to guardians when student is absent
     */
    public void sendAbsenteeNotification(Student student, List<Guardian> guardians) {
        if (!whatsappEnabled) {
            logger.info("WhatsApp is disabled. Skipping absentee notification for student: {}", student.getStudentId());
            return;
        }

        String message = createAbsenteeMessage(student);
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        for (Guardian guardian : guardians) {
            if (guardian.getWhatsappNumber() != null && !guardian.getWhatsappNumber().isEmpty()) {
                try {
                    sendWhatsAppMessage(guardian.getWhatsappNumber(), message);
                    logger.info("Sent absentee notification to guardian {} for student {}", 
                              guardian.getName(), student.getStudentId());
                } catch (Exception e) {
                    logger.error("Failed to send absentee notification to guardian {} for student {}: {}", 
                               guardian.getName(), student.getStudentId(), e.getMessage());
                }
            }
        }
    }

    /**
     * Send assignment document to all students in a class
     */
    public void sendAssignmentToClass(String form, String section, String subjectName, 
                                    String assignmentTitle, String documentUrl, String teacherName) {
        if (!whatsappEnabled) {
            logger.info("WhatsApp is disabled. Skipping assignment distribution to class: {} {}", form, section);
            return;
        }

        String message = createAssignmentMessage(subjectName, assignmentTitle, teacherName, documentUrl);
        
        // This would be called from the AI WhatsApp sharing service
        // The actual student list would be retrieved from the database
        logger.info("Assignment message prepared for class {} {}: {}", form, section, assignmentTitle);
    }

    /**
     * Send general message to a phone number
     */
    public void sendMessage(String phoneNumber, String message) {
        if (!whatsappEnabled) {
            logger.info("WhatsApp is disabled. Skipping message to: {}", phoneNumber);
            return;
        }

        try {
            sendWhatsAppMessage(phoneNumber, message);
            logger.info("Message sent successfully to: {}", phoneNumber);
        } catch (Exception e) {
            logger.error("Failed to send message to {}: {}", phoneNumber, e.getMessage());
        }
    }

    /**
     * Send document via WhatsApp
     */
    public void sendDocument(String phoneNumber, String message, String documentUrl, String documentName) {
        if (!whatsappEnabled) {
            logger.info("WhatsApp is disabled. Skipping document to: {}", phoneNumber);
            return;
        }

        try {
            sendWhatsAppDocument(phoneNumber, message, documentUrl, documentName);
            logger.info("Document sent successfully to: {}", phoneNumber);
        } catch (Exception e) {
            logger.error("Failed to send document to {}: {}", phoneNumber, e.getMessage());
        }
    }

    /**
     * Send WhatsApp message with retry logic
     */
    private void sendWhatsAppMessage(String phoneNumber, String message) {
        if (!twilioConfig.isWhatsAppEnabled()) {
            logger.warn("Twilio WhatsApp is not properly configured");
            return;
        }

        String formattedPhone = formatPhoneNumber(phoneNumber);
        
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhone),
                    new PhoneNumber(fromNumber),
                    message
                ).create();
                
                logger.info("WhatsApp message sent successfully to {} (attempt {})", formattedPhone, attempt);
                return;
                
            } catch (Exception e) {
                logger.error("Attempt {} failed to send WhatsApp message to {}: {}", 
                           attempt, formattedPhone, e.getMessage());
                
                if (attempt < retryAttempts) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed to send WhatsApp message after " + retryAttempts + " attempts");
    }

    /**
     * Send WhatsApp document with retry logic
     */
    private void sendWhatsAppDocument(String phoneNumber, String message, String documentUrl, String documentName) {
        if (!twilioConfig.isWhatsAppEnabled()) {
            logger.warn("Twilio WhatsApp is not properly configured");
            return;
        }

        String formattedPhone = formatPhoneNumber(phoneNumber);
        
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhone),
                    new PhoneNumber(fromNumber),
                    message
                )
                .setMediaUrl(URI.create(documentUrl))
                .create();
                
                logger.info("WhatsApp document sent successfully to {} (attempt {})", formattedPhone, attempt);
                return;
                
            } catch (Exception e) {
                logger.error("Attempt {} failed to send WhatsApp document to {}: {}", 
                           attempt, formattedPhone, e.getMessage());
                
                if (attempt < retryAttempts) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed to send WhatsApp document after " + retryAttempts + " attempts");
    }

    /**
     * Create absentee notification message
     */
    private String createAbsenteeMessage(Student student) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        return String.format(
            """
            📚 *Attendance Notification*
            
            Dear Parent/Guardian,
            
            We would like to inform you that your child *%s %s* (ID: %s) was marked *absent* from school today (%s).
            
            *Class:* %s %s
            *Date:* %s
            
            If this is an error or if you have any concerns, please contact the school immediately.
            
            Thank you,
            School Administration""",
            student.getFirstName(),
            student.getLastName(),
            student.getStudentId(),
            currentDate,
            student.getForm(),
            student.getSection(),
            currentDate
        );
    }

    /**
     * Create assignment notification message
     */
    private String createAssignmentMessage(String subjectName, String assignmentTitle, String teacherName, String documentUrl) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        return String.format(
            """
            📝 *New Assignment*
            
            *Subject:* %s
            *Title:* %s
            *Teacher:* %s
            *Date:* %s
            
            Please find the assignment document attached.
            
            Complete and submit by the due date.
            
            Good luck! 📚""",
            subjectName,
            assignmentTitle,
            teacherName,
            currentDate
        );
    }

    /**
     * Format phone number for WhatsApp
     */
    private String formatPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        // Add country code if not present (assuming Zimbabwe +263)
        if (cleaned.startsWith("0")) {
            cleaned = "263" + cleaned.substring(1);
        } else if (!cleaned.startsWith("263")) {
            cleaned = "263" + cleaned;
        }
        
        return cleaned;
    }
}
