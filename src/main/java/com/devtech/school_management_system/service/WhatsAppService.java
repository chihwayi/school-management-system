package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsAppService {

    public void sendAbsenteeNotification(Student student, List<Guardian> guardians) {
        // Implementation for sending WhatsApp messages
        // This would integrate with a WhatsApp Business API service

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
                sendWhatsAppMessage(guardian.getWhatsappNumber(), message);
            }
        }
    }

    public void sendMessage(String phoneNumber, String message) {
        sendWhatsAppMessage(phoneNumber, message);
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        // Placeholder for WhatsApp Business API integration
    }
}
