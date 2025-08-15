package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.SignatureDTO;
import com.devtech.school_management_system.service.SignatureService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/signatures", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignatureController {
    
    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public SignatureDTO uploadSignature(@RequestParam("file") MultipartFile file,
                                       Authentication authentication) {
        return signatureService.uploadSignature(file, authentication.getName());
    }

    @GetMapping("/my-signature")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public SignatureDTO getMySignature(Authentication authentication) {
        return signatureService.getUserSignature(authentication.getName());
    }

    @GetMapping("/principal")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_CLERK')")
    public SignatureDTO getPrincipalSignature() {
        return signatureService.getPrincipalSignature();
    }

    @GetMapping("/class-teacher/{form}/{section}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_CLERK')")
    public SignatureDTO getClassTeacherSignature(@PathVariable String form, @PathVariable String section) {
        return signatureService.getClassTeacherSignature(form, section);
    }

    @GetMapping("/subject-teacher/{subjectId}/{form}/{section}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_CLERK')")
    public SignatureDTO getSubjectTeacherSignature(@PathVariable Long subjectId, 
                                                  @PathVariable String form, 
                                                  @PathVariable String section) {
        return signatureService.getSubjectTeacherSignature(subjectId, form, section);
    }
}