package com.devtech.admin.service;

import com.devtech.admin.dto.DashboardStatsDTO;
import com.devtech.admin.dto.SchoolStatsDTO;
import com.devtech.admin.repository.SchoolRepository;
import com.devtech.admin.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Mock data for now - replace with actual database queries
        stats.setTotalSchools(15L);
        stats.setTotalUsers(2450L);
        stats.setMonthlyRevenue(new BigDecimal("1500.00"));
        stats.setUptime(99.9);
        stats.setSchoolsGrowth(12.5);
        stats.setUsersGrowth(8.3);
        stats.setRevenueGrowth(15.2);
        stats.setUptimeChange(0.1);

        // Schools by plan
        List<Map<String, Object>> schoolsByPlan = new ArrayList<>();
        schoolsByPlan.add(Map.of("name", "Basic", "value", 8));
        schoolsByPlan.add(Map.of("name", "Premium", "value", 5));
        schoolsByPlan.add(Map.of("name", "Enterprise", "value", 2));
        stats.setSchoolsByPlan(schoolsByPlan);

        return stats;
    }

    public Map<String, Object> getRevenueStats() {
        Map<String, Object> revenueStats = new HashMap<>();
        
        // Mock monthly revenue data
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        monthlyData.add(Map.of("month", "Jan", "revenue", 1200));
        monthlyData.add(Map.of("month", "Feb", "revenue", 1350));
        monthlyData.add(Map.of("month", "Mar", "revenue", 1420));
        monthlyData.add(Map.of("month", "Apr", "revenue", 1500));
        monthlyData.add(Map.of("month", "May", "revenue", 1580));
        monthlyData.add(Map.of("month", "Jun", "revenue", 1650));
        
        revenueStats.put("monthlyData", monthlyData);
        revenueStats.put("totalRevenue", 8620);
        revenueStats.put("growthRate", 15.2);
        
        return revenueStats;
    }

    public List<SchoolStatsDTO> getActiveSchools() {
        List<SchoolStatsDTO> activeSchools = new ArrayList<>();
        
        // Mock active schools data
        activeSchools.add(new SchoolStatsDTO("SCH001", "St. Mary's High", "stmarys", "premium", "active"));
        activeSchools.add(new SchoolStatsDTO("SCH002", "Central Academy", "central", "basic", "active"));
        activeSchools.add(new SchoolStatsDTO("SCH003", "Elite College", "elite", "enterprise", "active"));
        activeSchools.add(new SchoolStatsDTO("SCH004", "Community School", "community", "basic", "active"));
        activeSchools.add(new SchoolStatsDTO("SCH005", "International Academy", "international", "premium", "active"));
        
        return activeSchools;
    }

    public List<SchoolStatsDTO> getRecentSchools() {
        List<SchoolStatsDTO> recentSchools = new ArrayList<>();
        
        // Mock recent schools data
        SchoolStatsDTO school1 = new SchoolStatsDTO("SCH006", "New Horizon School", "newhorizon", "basic", "active");
        school1.setCreatedAt(LocalDateTime.now().minusDays(2));
        recentSchools.add(school1);
        
        SchoolStatsDTO school2 = new SchoolStatsDTO("SCH007", "Future Leaders", "futureleaders", "premium", "active");
        school2.setCreatedAt(LocalDateTime.now().minusDays(5));
        recentSchools.add(school2);
        
        return recentSchools;
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> systemHealth = new HashMap<>();
        
        systemHealth.put("overallStatus", "healthy");
        systemHealth.put("uptime", 99.9);
        systemHealth.put("responseTime", 120);
        systemHealth.put("errorRate", 0.01);
        
        Map<String, String> services = new HashMap<>();
        services.put("database", "healthy");
        services.put("backend", "healthy");
        services.put("frontend", "healthy");
        services.put("monitoring", "healthy");
        
        systemHealth.put("services", services);
        
        return systemHealth;
    }

    public Map<String, Object> getMonthlyAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("newSchools", 3);
        analytics.put("activeUsers", 2450);
        analytics.put("revenue", 1500);
        analytics.put("growth", 12.5);
        
        return analytics;
    }

    public Map<String, Object> getUsageAnalytics() {
        Map<String, Object> usage = new HashMap<>();
        
        Map<String, Integer> featureUsage = new HashMap<>();
        featureUsage.put("student_management", 95);
        featureUsage.put("attendance", 88);
        featureUsage.put("reports", 92);
        featureUsage.put("fees", 85);
        
        usage.put("featureUsage", featureUsage);
        usage.put("totalLogins", 1250);
        usage.put("averageSessionTime", 45);
        
        return usage;
    }
}








