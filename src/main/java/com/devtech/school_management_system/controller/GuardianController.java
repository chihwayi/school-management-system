package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.repository.GuardianRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value ="/api/guardians", produces = MediaType.APPLICATION_JSON_VALUE)
public class GuardianController {
    private final GuardianRepository guardianRepository;
    private final StudentRepository studentRepository;

    public GuardianController(GuardianRepository guardianRepository,
                              StudentRepository studentRepository) {
        this.guardianRepository = guardianRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/student/{studentId}")
    public List<Guardian> getGuardiansByStudent(@PathVariable Long studentId) {
        return guardianRepository.findByStudentId(studentId);
    }

    @PostMapping("/student/{studentId}")
    public Guardian addGuardianToStudent(@PathVariable Long studentId,
                                         @RequestBody Guardian guardian) {
        return studentRepository.findById(studentId).map(student -> {
            guardian.setStudent(student);
            return guardianRepository.save(guardian);
        }).orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @PutMapping("/{id}")
    public Guardian updateGuardian(@PathVariable Long id,
                                   @RequestBody Guardian guardianDetails) {
        Guardian guardian = guardianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guardian not found"));

        guardian.setName(guardianDetails.getName());
        guardian.setRelationship(guardianDetails.getRelationship());
        guardian.setPhoneNumber(guardianDetails.getPhoneNumber());
        guardian.setWhatsappNumber(guardianDetails.getWhatsappNumber());
        guardian.setPrimaryGuardian(guardianDetails.isPrimaryGuardian());

        return guardianRepository.save(guardian);
    }

    @DeleteMapping("/{id}")
    public void deleteGuardian(@PathVariable Long id) {
        guardianRepository.deleteById(id);
    }

    @GetMapping("/guardian/{id}")
    public Guardian getGuardian(@PathVariable Long id) {
        return guardianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guardian not found with id: " + id));
    }
}

