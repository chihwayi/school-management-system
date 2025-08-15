package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.FeeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeSettingRepository extends JpaRepository<FeeSetting, Long> {
    
    List<FeeSetting> findByActiveTrue();
    
    Optional<FeeSetting> findByLevelAndAcademicYearAndTermAndActiveTrue(
        String level, String academicYear, String term);
    
    List<FeeSetting> findByLevelAndActiveTrue(String level);
    
    List<FeeSetting> findByAcademicYearAndTermAndActiveTrue(String academicYear, String term);
}