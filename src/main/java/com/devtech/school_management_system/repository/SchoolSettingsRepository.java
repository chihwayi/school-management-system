package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.SchoolSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolSettingsRepository extends JpaRepository<SchoolSettings, Long> {
    // Find the first (and usually only) school settings record
    SchoolSettings findFirstByOrderByIdAsc();
}