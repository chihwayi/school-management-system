package com.devtech.school_management_system.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenant = extractTenantFromRequest(request);
        
        System.out.println("TenantInterceptor - Host: " + request.getHeader("Host") + 
                          ", Path: " + request.getRequestURI() + 
                          ", Tenant: " + tenant);
        
        if (tenant != null) {
            TenantContext.setCurrentTenant(tenant);
            System.out.println("TenantInterceptor - Set tenant: " + tenant + 
                              ", Database: " + TenantContext.getCurrentDatabase());
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clean up the tenant context
        TenantContext.clear();
    }

    private String extractTenantFromRequest(HttpServletRequest request) {
        // Method 1: Check URL parameter (for development)
        String tenantParam = request.getParameter("tenant");
        if (tenantParam != null && !tenantParam.trim().isEmpty()) {
            System.out.println("TenantInterceptor - Found tenant in URL parameter: " + tenantParam);
            return tenantParam.replace("-", "_"); // Convert test-school to test_school
        }

        // Method 2: Check subdomain (for localhost development)
        String host = request.getHeader("Host");
        if (host != null && host.contains(".localhost")) {
            String[] parts = host.split("\\.");
            if (parts.length >= 2 && !"localhost".equals(parts[0])) {
                String subdomain = parts[0];
                System.out.println("TenantInterceptor - Found tenant in localhost subdomain: " + subdomain);
                return subdomain.replace("-", "_");
            }
        }

        // Method 3: Check subdomain (for production)
        if (host != null && !host.contains("localhost")) {
            String[] parts = host.split("\\.");
            if (parts.length > 2) {
                String subdomain = parts[0];
                System.out.println("TenantInterceptor - Found tenant in production subdomain: " + subdomain);
                return subdomain.replace("-", "_");
            }
        }

        System.out.println("TenantInterceptor - No tenant found, using default database");
        return null; // Will use default database
    }
}
