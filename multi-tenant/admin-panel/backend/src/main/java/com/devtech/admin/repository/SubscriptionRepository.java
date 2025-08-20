package com.devtech.admin.repository;

import com.devtech.admin.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findBySchoolId(String schoolId);
    
    List<Subscription> findByStatus(String status);
    
    List<Subscription> findByPlanType(String planType);
    
    @Query("SELECT SUM(s.monthlyFee) FROM Subscription s WHERE s.status = 'active'")
    BigDecimal getTotalMonthlyRevenue();
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'active'")
    Long countActiveSubscriptions();
    
    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate <= :date")
    List<Subscription> findUpcomingBilling(LocalDate date);
}








