package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.GuardianRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public GuardianService(GuardianRepository guardianRepository,
                           StudentRepository studentRepository) {
        this.guardianRepository = guardianRepository;
        this.studentRepository = studentRepository;
    }

    public List<Guardian> getGuardiansByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return guardianRepository.findByStudentId(studentId);
    }

    public Guardian addGuardianToStudent(Long studentId, Guardian guardian) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // If this is being set as primary guardian, ensure no other guardian is primary
        if (guardian.isPrimaryGuardian()) {
            List<Guardian> existingGuardians = guardianRepository.findByStudentId(studentId);
            existingGuardians.forEach(g -> {
                if (g.isPrimaryGuardian()) {
                    g.setPrimaryGuardian(false);
                    guardianRepository.save(g);
                }
            });
        }

        guardian.setStudent(student);
        return guardianRepository.save(guardian);
    }

    public Guardian updateGuardian(Long guardianId, Guardian guardianDetails) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + guardianId));

        guardian.setName(guardianDetails.getName());
        guardian.setRelationship(guardianDetails.getRelationship());
        guardian.setPhoneNumber(guardianDetails.getPhoneNumber());
        guardian.setWhatsappNumber(guardianDetails.getWhatsappNumber());

        // If this is being set as primary guardian, ensure no other guardian is primary
        if (guardianDetails.isPrimaryGuardian() && !guardian.isPrimaryGuardian()) {
            List<Guardian> existingGuardians = guardianRepository.findByStudentId(guardian.getStudent().getId());
            existingGuardians.forEach(g -> {
                if (g.isPrimaryGuardian() && !g.getId().equals(guardianId)) {
                    g.setPrimaryGuardian(false);
                    guardianRepository.save(g);
                }
            });
        }

        guardian.setPrimaryGuardian(guardianDetails.isPrimaryGuardian());

        return guardianRepository.save(guardian);
    }

    public void deleteGuardian(Long guardianId) {
        Guardian guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + guardianId));
        guardianRepository.delete(guardian);
    }

    public Guardian getGuardianById(Long guardianId) {
        return guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + guardianId));
    }

    public Guardian getPrimaryGuardian(Long studentId) {
        List<Guardian> guardians = getGuardiansByStudent(studentId);
        return guardians.stream()
                .filter(Guardian::isPrimaryGuardian)
                .findFirst()
                .orElse(guardians.isEmpty() ? null : guardians.get(0));
    }

    public List<Guardian> getAllGuardians() {
        return guardianRepository.findAll();
    }

    public List<Guardian> getGuardiansByPhoneNumber(String phoneNumber) {
        return guardianRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Guardian> getGuardiansByWhatsappNumber(String whatsappNumber) {
        return guardianRepository.findByWhatsappNumber(whatsappNumber);
    }

    public boolean hasGuardians(Long studentId) {
        return !getGuardiansByStudent(studentId).isEmpty();
    }

    public void setPrimaryGuardian(Long guardianId) {
        Guardian guardian = getGuardianById(guardianId);
        Long studentId = guardian.getStudent().getId();

        // Remove primary status from all other guardians of this student
        List<Guardian> allGuardians = getGuardiansByStudent(studentId);
        allGuardians.forEach(g -> {
            if (!g.getId().equals(guardianId)) {
                g.setPrimaryGuardian(false);
                guardianRepository.save(g);
            }
        });

        // Set this guardian as primary
        guardian.setPrimaryGuardian(true);
        guardianRepository.save(guardian);
    }
}