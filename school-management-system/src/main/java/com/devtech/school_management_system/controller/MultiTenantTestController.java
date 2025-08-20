package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/multi-tenant")
@CrossOrigin(origins = "*")
public class MultiTenantTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public Map<String, Object> testMultiTenant(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Manual tenant detection
            String tenantParam = request.getParameter("tenant");
            if (tenantParam != null && !tenantParam.trim().isEmpty()) {
                String tenant = tenantParam.replace("-", "_");
                TenantContext.setCurrentTenant(tenant);
                response.put("tenantSet", true);
                response.put("tenant", tenant);
                response.put("database", TenantContext.getCurrentDatabase());
            } else {
                response.put("tenantSet", false);
                response.put("tenant", "default");
                response.put("database", "school_management_system");
            }

            // Test database connection
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            response.put("actualDatabase", currentDb);
            
            // Test if school_test_school database exists
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'school_test_school'", 
                    Integer.class
                );
                response.put("testSchoolDbExists", count > 0);
            } catch (Exception e) {
                response.put("testSchoolDbExists", false);
                response.put("dbCheckError", e.getMessage());
            }

            response.put("success", true);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } finally {
            TenantContext.clear();
        }
        
        return response;
    }
}