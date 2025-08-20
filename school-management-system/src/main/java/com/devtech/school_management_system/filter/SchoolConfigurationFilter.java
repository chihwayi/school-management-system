package com.devtech.school_management_system.filter;

import com.devtech.school_management_system.service.SchoolServiceImpl;
import com.devtech.school_management_system.service.TenantManagementService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SchoolConfigurationFilter extends OncePerRequestFilter {

    private static final List<String> SETUP_ENDPOINTS = Arrays.asList(
            "/api/school/config",
            "/api/school/setup",
            "/api/uploads/",
            "/api/tenant/"
    );

    private static final List<String> AUTH_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register"
    );

    private final SchoolServiceImpl schoolService;
    private final TenantManagementService tenantManagementService;

    @Autowired
    public SchoolConfigurationFilter(SchoolServiceImpl schoolService, TenantManagementService tenantManagementService) {
        this.schoolService = schoolService;
        this.tenantManagementService = tenantManagementService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // If the request is for static resources or setup endpoints, proceed normally
        System.out.println("SchoolConfigurationFilter - Checking path: " + path + ", isSetupEndpoint: " + isSetupEndpoint(path) + ", isStaticResource: " + isStaticResource(path));
        if (isSetupEndpoint(path) || isStaticResource(path)) {
            System.out.println("SchoolConfigurationFilter - Allowing setup/static endpoint: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Check if school is configured
        boolean isSchoolConfigured = schoolService.isSchoolConfigured();
        System.out.println("SchoolConfigurationFilter - Path: " + path + ", School configured: " + isSchoolConfigured);

        // If school is not configured, check if there's a tenant database
        if (!isSchoolConfigured) {
            System.out.println("SchoolConfigurationFilter - School not configured, checking tenant database");
            // Check if tenant database exists and configure school
            boolean tenantConfigured = tenantManagementService.checkAndConfigureTenantDatabase();
            System.out.println("SchoolConfigurationFilter - Tenant configured: " + tenantConfigured);
            if (tenantConfigured) {
                // Tenant database was found and configured, continue with the request
                System.out.println("SchoolConfigurationFilter - Tenant database found, continuing request");
                filterChain.doFilter(request, response);
                return;
            }
        }

        // If school is not configured and trying to access non-setup endpoints
        if (!isSchoolConfigured && !isSetupEndpoint(path)) {
            // If this is an auth endpoint, return error as JSON
            if (isAuthEndpoint(path)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\":\"School setup is required before authentication\",\"setupRequired\":true}");
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }

            // Allow tenant, test, and school admin endpoints even when school is not configured
            if (path.startsWith("/api/tenant/") || path.startsWith("/api/test/") || path.startsWith("/api/school-admin/")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Redirect to setup endpoint for all other requests
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", "/setup");
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private boolean isSetupEndpoint(String path) {
        return SETUP_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isAuthEndpoint(String path) {
        return AUTH_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/assets/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/api/uploads/") ||
                path.equals("/favicon.ico") ||
                path.equals("/") ||
                path.startsWith("/setup");
    }
}