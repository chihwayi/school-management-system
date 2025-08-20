package com.devtech.school_management_system.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
            "message", "Hello from School Management System!",
            "status", "success",
            "timestamp", System.currentTimeMillis()
        );
    }

    @GetMapping("/tenant")
    public Map<String, Object> testTenant(jakarta.servlet.http.HttpServletRequest request) {
        // Manually extract tenant for testing
        String tenantParam = request.getParameter("tenant");
        if (tenantParam != null) {
            tenantParam = tenantParam.replace("-", "_");
            com.devtech.school_management_system.config.TenantContext.setCurrentTenant(tenantParam);
        }
        
        String tenant = com.devtech.school_management_system.config.TenantContext.getCurrentTenant();
        String database = com.devtech.school_management_system.config.TenantContext.getCurrentDatabase();
        
        return Map.of(
            "tenant", tenant != null ? tenant : "default",
            "database", database != null ? database : "school_management_system",
            "tenantParam", tenantParam != null ? tenantParam : "none",
            "timestamp", System.currentTimeMillis()
        );
    }
}
