package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.FeeSettingDTO;
import com.devtech.school_management_system.service.FeeSettingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees/settings")
public class FeeSettingController {
    
    private final FeeSettingService feeSettingService;
    
    public FeeSettingController(FeeSettingService feeSettingService) {
        this.feeSettingService = feeSettingService;
    }
    
    @GetMapping
    public ResponseEntity<List<FeeSettingDTO>> getAllFeeSettings() {
        return ResponseEntity.ok(feeSettingService.getAllFeeSettings());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<FeeSettingDTO>> getActiveFeeSettings() {
        return ResponseEntity.ok(feeSettingService.getActiveFeeSettings());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FeeSettingDTO> getFeeSettingById(@PathVariable Long id) {
        return ResponseEntity.ok(feeSettingService.getFeeSettingById(id));
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<FeeSettingDTO> getFeeSettingByLevel(
            @PathVariable String level,
            @RequestParam String academicYear,
            @RequestParam String term) {
        return ResponseEntity.ok(feeSettingService.getFeeSettingByLevel(level, academicYear, term));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<FeeSettingDTO> createFeeSetting(@RequestBody FeeSettingDTO feeSettingDTO) {
        return new ResponseEntity<>(feeSettingService.createFeeSetting(feeSettingDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<FeeSettingDTO> updateFeeSetting(
            @PathVariable Long id,
            @RequestBody FeeSettingDTO feeSettingDTO) {
        return ResponseEntity.ok(feeSettingService.updateFeeSetting(id, feeSettingDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteFeeSetting(@PathVariable Long id) {
        feeSettingService.deleteFeeSetting(id);
        return ResponseEntity.noContent().build();
    }
}