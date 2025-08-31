package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.StudentParentAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentParentAuthRepository extends JpaRepository<StudentParentAuth, Long> {
    
    Optional<StudentParentAuth> findByMobileNumberAndUserType(String mobileNumber, StudentParentAuth.UserType userType);
    
    Optional<StudentParentAuth> findByMobileNumber(String mobileNumber);
    
    @Query("SELECT spa FROM StudentParentAuth spa WHERE spa.referenceId = :referenceId AND spa.userType = :userType")
    Optional<StudentParentAuth> findByReferenceIdAndUserType(@Param("referenceId") Long referenceId, @Param("userType") StudentParentAuth.UserType userType);
    
    @Query("SELECT spa FROM StudentParentAuth spa WHERE spa.userType = :userType AND spa.isActive = true")
    List<StudentParentAuth> findAllActiveByUserType(@Param("userType") StudentParentAuth.UserType userType);
    
    boolean existsByMobileNumberAndUserType(String mobileNumber, StudentParentAuth.UserType userType);
    
    boolean existsByMobileNumber(String mobileNumber);
}
