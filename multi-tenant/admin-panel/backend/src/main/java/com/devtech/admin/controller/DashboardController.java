package com.devtech.admin.controller;

import com.devtech.admin.dto.DashboardStatsDTO;
import com.devtech.admin.dto.SchoolStatsDTO;
import com.devtech.admin.service.DashboardService;
import com.devtech.admin.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        Map<String, Object> revenueStats = dashboardService.getRevenueStats();
        return ResponseEntity.ok(revenueStats);
    }

    @GetMapping("/schools/active")
    public ResponseEntity<List<SchoolStatsDTO>> getActiveSchools() {
        List<SchoolStatsDTO> activeSchools = dashboardService.getActiveSchools();
        return ResponseEntity.ok(activeSchools);
    }

    @GetMapping("/schools/recent")
    public ResponseEntity<List<SchoolStatsDTO>> getRecentSchools() {
        List<SchoolStatsDTO> recentSchools = dashboardService.getRecentSchools();
        return ResponseEntity.ok(recentSchools);
    }

    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> systemHealth = dashboardService.getSystemHealth();
        return ResponseEntity.ok(systemHealth);
    }

    @GetMapping("/analytics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyAnalytics() {
        Map<String, Object> monthlyAnalytics = dashboardService.getMonthlyAnalytics();
        return ResponseEntity.ok(monthlyAnalytics);
    }

    @GetMapping("/analytics/usage")
    public ResponseEntity<Map<String, Object>> getUsageAnalytics() {
        Map<String, Object> usageAnalytics = dashboardService.getUsageAnalytics();
        return ResponseEntity.ok(usageAnalytics);
    }
}

