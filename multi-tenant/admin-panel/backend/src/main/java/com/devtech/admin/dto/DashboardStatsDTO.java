package com.devtech.admin.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    private Long totalSchools;
    private Long totalUsers;
    private BigDecimal monthlyRevenue;
    private Double uptime;
    private Double schoolsGrowth;
    private Double usersGrowth;
    private Double revenueGrowth;
    private Double uptimeChange;
    private List<Map<String, Object>> schoolsByPlan;
    private List<Map<String, Object>> recentActivity;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(Long totalSchools, Long totalUsers, BigDecimal monthlyRevenue, Double uptime) {
        this.totalSchools = totalSchools;
        this.totalUsers = totalUsers;
        this.monthlyRevenue = monthlyRevenue;
        this.uptime = uptime;
    }

    // Getters and Setters
    public Long getTotalSchools() {
        return totalSchools;
    }

    public void setTotalSchools(Long totalSchools) {
        this.totalSchools = totalSchools;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Double getUptime() {
        return uptime;
    }

    public void setUptime(Double uptime) {
        this.uptime = uptime;
    }

    public Double getSchoolsGrowth() {
        return schoolsGrowth;
    }

    public void setSchoolsGrowth(Double schoolsGrowth) {
        this.schoolsGrowth = schoolsGrowth;
    }

    public Double getUsersGrowth() {
        return usersGrowth;
    }

    public void setUsersGrowth(Double usersGrowth) {
        this.usersGrowth = usersGrowth;
    }

    public Double getRevenueGrowth() {
        return revenueGrowth;
    }

    public void setRevenueGrowth(Double revenueGrowth) {
        this.revenueGrowth = revenueGrowth;
    }

    public Double getUptimeChange() {
        return uptimeChange;
    }

    public void setUptimeChange(Double uptimeChange) {
        this.uptimeChange = uptimeChange;
    }

    public List<Map<String, Object>> getSchoolsByPlan() {
        return schoolsByPlan;
    }

    public void setSchoolsByPlan(List<Map<String, Object>> schoolsByPlan) {
        this.schoolsByPlan = schoolsByPlan;
    }

    public List<Map<String, Object>> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<Map<String, Object>> recentActivity) {
        this.recentActivity = recentActivity;
    }
}








