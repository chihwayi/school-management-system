package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.StudentParentLoginRequest;
import com.devtech.school_management_system.dto.FirstTimeLoginRequest;
import com.devtech.school_management_system.dto.StudentParentProfileDTO;
import com.devtech.school_management_system.entity.StudentParentAuth;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.repository.StudentParentAuthRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.GuardianRepository;
import com.devtech.school_management_system.repository.ReportRepository;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentParentAuthService {
    
    @Autowired
    private StudentParentAuthRepository authRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private GuardianRepository guardianRepository;
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private FeePaymentRepository feePaymentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public boolean checkFirstTimeLogin(String mobileNumber, String userType) {
        Optional<StudentParentAuth> auth = authRepository.findByMobileNumberAndUserType(
            mobileNumber, 
            StudentParentAuth.UserType.valueOf(userType.toUpperCase())
        );
        return auth.map(StudentParentAuth::isFirstTime).orElse(true);
    }
    
    @Transactional
    public String firstTimeLogin(FirstTimeLoginRequest request) {
        String mobileNumber = request.getMobileNumber();
        String userType = request.getUserType().toUpperCase();
        
        // Find the student or guardian by mobile number
        Long referenceId = null;
        
        if (userType.equals("STUDENT")) {
            Student student = studentRepository.findByWhatsappNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Student not found with mobile number: " + mobileNumber));
            referenceId = student.getId();
        } else if (userType.equals("PARENT")) {
            List<Guardian> guardians = guardianRepository.findByWhatsappNumber(mobileNumber);
            if (guardians.isEmpty()) {
                throw new RuntimeException("Guardian not found with mobile number: " + mobileNumber);
            }
            Guardian guardian = guardians.get(0);
            referenceId = guardian.getId();
        }
        
        // Create or update auth record
        StudentParentAuth auth = authRepository.findByMobileNumberAndUserType(
            mobileNumber, 
            StudentParentAuth.UserType.valueOf(userType)
        ).orElse(new StudentParentAuth(
            StudentParentAuth.UserType.valueOf(userType),
            mobileNumber,
            passwordEncoder.encode(request.getPassword()),
            referenceId
        ));
        
        auth.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        auth.setFirstTime(false);
        auth.setActive(true);
        
        authRepository.save(auth);
        
        // Generate proper JWT token using the new method
        return jwtTokenProvider.generateStudentParentToken(mobileNumber, userType, referenceId);
    }
    
    @Transactional
    public String login(StudentParentLoginRequest request) {
        String mobileNumber = request.getMobileNumber();
        String userType = request.getUserType().toUpperCase();
        
        Optional<StudentParentAuth> auth = authRepository.findByMobileNumberAndUserType(
            mobileNumber, 
            StudentParentAuth.UserType.valueOf(userType)
        );
        
        if (auth.isPresent() && auth.get().isActive()) {
            StudentParentAuth authEntity = auth.get();
            
            if (passwordEncoder.matches(request.getPassword(), authEntity.getPasswordHash())) {
                // Update last login
                authEntity.setLastLogin(LocalDateTime.now());
                authRepository.save(authEntity);
                
                // Generate proper JWT token
                return jwtTokenProvider.generateStudentParentToken(mobileNumber, userType, authEntity.getReferenceId());
            }
        }
        
        throw new RuntimeException("Invalid credentials");
    }
    
    @Transactional(readOnly = true)
    public StudentParentProfileDTO getStudentProfile(Long studentId) {
        // Simplified implementation for now
        StudentParentProfileDTO.StudentInfoDTO studentInfo = new StudentParentProfileDTO.StudentInfoDTO(
            studentId,
            "Student " + studentId,
            "STU" + studentId,
            "Form 1",
            "A",
            "Secondary",
            "+1234567890",
            true,
            0.0
        );
        
        StudentParentProfileDTO profile = new StudentParentProfileDTO(
            studentId,
            "Student " + studentId,
            "+1234567890",
            "STUDENT"
        );
        profile.setStudentInfo(studentInfo);
        
        return profile;
    }
    
    @Transactional(readOnly = true)
    public StudentParentProfileDTO getParentProfile(Long guardianId) {
        // Simplified implementation for now
        List<StudentParentProfileDTO.ChildInfoDTO> childrenInfo = List.of(
            new StudentParentProfileDTO.ChildInfoDTO(
                1L,
                "Child 1",
                "STU001",
                "Form 1",
                "A",
                "Secondary",
                0.0,
                true
            ),
            new StudentParentProfileDTO.ChildInfoDTO(
                2L,
                "Child 2",
                "STU002",
                "Form 2",
                "B",
                "Secondary",
                50.0,
                false
            )
        );
        
        StudentParentProfileDTO profile = new StudentParentProfileDTO(
            guardianId,
            "Parent " + guardianId,
            "+1234567890",
            "PARENT"
        );
        profile.setChildren(childrenInfo);
        
        return profile;
    }
    

}
