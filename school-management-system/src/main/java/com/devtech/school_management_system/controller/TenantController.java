package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.service.TenantManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant")
@CrossOrigin(origins = "*")
public class TenantController {

    @Autowired
    private TenantManagementService tenantManagementService;

    /**
     * Register a new tenant/school from the admin panel
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerTenant(@RequestBody Map<String, String> request) {
        try {
            String tenantId = request.get("tenantId");
            String databaseName = request.get("databaseName");
            
            if (tenantId == null || databaseName == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "tenantId and databaseName are required"));
            }
            
            // Create the tenant database
            tenantManagementService.createTenantDatabase(tenantId, databaseName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tenant registered successfully",
                "tenantId", tenantId,
                "databaseName", databaseName
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to register tenant: " + e.getMessage()));
        }
    }

    /**
     * Get current tenant information
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentTenant() {
        String currentTenant = tenantManagementService.getCurrentTenant();
        
        return ResponseEntity.ok(Map.of(
            "currentTenant", currentTenant,
            "isMultiTenant", currentTenant != null
        ));
    }

    /**
     * Remove a tenant (for admin panel use)
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Map<String, Object>> removeTenant(@PathVariable String tenantId) {
        try {
            tenantManagementService.removeTenantDatabase(tenantId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Tenant removed successfully",
                "tenantId", tenantId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to remove tenant: " + e.getMessage()));
        }
    }
}
