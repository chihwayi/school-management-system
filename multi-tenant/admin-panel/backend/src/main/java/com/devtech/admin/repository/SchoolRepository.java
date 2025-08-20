package com.devtech.admin.repository;

import com.devtech.admin.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    
    Optional<School> findBySchoolId(String schoolId);
    
    Optional<School> findBySubdomain(String subdomain);
    
    List<School> findByStatus(String status);
    
    List<School> findByPlanType(String planType);
    
    @Query("SELECT s FROM School s WHERE s.name LIKE %:query% OR s.subdomain LIKE %:query% OR s.adminEmail LIKE %:query%")
    List<School> searchSchools(String query);
    
    @Query("SELECT COUNT(s) FROM School s WHERE s.status = 'active'")
    Long countActiveSchools();
    
    @Query("SELECT COUNT(s) FROM School s WHERE s.planType = :planType")
    Long countByPlanType(String planType);
    
    @Query("SELECT COUNT(s) FROM School s WHERE s.status = :status")
    Long countByStatus(String status);
}
