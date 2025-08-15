package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.FeeSettingDTO;
import com.devtech.school_management_system.entity.FeeSetting;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.FeeSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeSettingService {
    
    private final FeeSettingRepository feeSettingRepository;
    
    public FeeSettingService(FeeSettingRepository feeSettingRepository) {
        this.feeSettingRepository = feeSettingRepository;
    }
    
    public List<FeeSettingDTO> getAllFeeSettings() {
        return feeSettingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FeeSettingDTO> getActiveFeeSettings() {
        return feeSettingRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public FeeSettingDTO getFeeSettingById(Long id) {
        FeeSetting feeSetting = feeSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee setting not found"));
        return convertToDTO(feeSetting);
    }
    
    public FeeSettingDTO getFeeSettingByLevel(String level, String academicYear, String term) {
        FeeSetting feeSetting = feeSettingRepository
                .findByLevelAndAcademicYearAndTermAndActiveTrue(level, academicYear, term)
                .orElseThrow(() -> new ResourceNotFoundException("Fee setting not found for the specified level"));
        return convertToDTO(feeSetting);
    }
    
    public FeeSettingDTO createFeeSetting(FeeSettingDTO feeSettingDTO) {
        FeeSetting feeSetting = convertToEntity(feeSettingDTO);
        FeeSetting savedFeeSetting = feeSettingRepository.save(feeSetting);
        return convertToDTO(savedFeeSetting);
    }
    
    public FeeSettingDTO updateFeeSetting(Long id, FeeSettingDTO feeSettingDTO) {
        FeeSetting existingFeeSetting = feeSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee setting not found"));
        
        existingFeeSetting.setAmount(feeSettingDTO.getAmount());
        existingFeeSetting.setAcademicYear(feeSettingDTO.getAcademicYear());
        existingFeeSetting.setTerm(feeSettingDTO.getTerm());
        existingFeeSetting.setActive(feeSettingDTO.isActive());
        
        FeeSetting updatedFeeSetting = feeSettingRepository.save(existingFeeSetting);
        return convertToDTO(updatedFeeSetting);
    }
    
    public void deleteFeeSetting(Long id) {
        if (!feeSettingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fee setting not found");
        }
        feeSettingRepository.deleteById(id);
    }
    
    private FeeSettingDTO convertToDTO(FeeSetting feeSetting) {
        return new FeeSettingDTO(
                feeSetting.getId(),
                feeSetting.getLevel(),
                feeSetting.getAmount(),
                feeSetting.getAcademicYear(),
                feeSetting.getTerm(),
                feeSetting.isActive()
        );
    }
    
    private FeeSetting convertToEntity(FeeSettingDTO feeSettingDTO) {
        FeeSetting feeSetting = new FeeSetting();
        feeSetting.setLevel(feeSettingDTO.getLevel());
        feeSetting.setAmount(feeSettingDTO.getAmount());
        feeSetting.setAcademicYear(feeSettingDTO.getAcademicYear());
        feeSetting.setTerm(feeSettingDTO.getTerm());
        feeSetting.setActive(feeSettingDTO.isActive());
        return feeSetting;
    }
}