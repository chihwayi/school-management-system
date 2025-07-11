package com.devtech.school_management_system.repository;


import com.devtech.school_management_system.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    List<Guardian> findByStudentId(Long studentId);

    @Query("SELECT g FROM Guardian g WHERE g.student.id = :studentId AND g.primaryGuardian = true")
    Optional<Guardian> findPrimaryGuardianByStudentId(@Param("studentId") Long studentId);

    List<Guardian> findByPhoneNumber(String phoneNumber);

    List<Guardian> findByWhatsappNumber(String whatsappNumber);
}
