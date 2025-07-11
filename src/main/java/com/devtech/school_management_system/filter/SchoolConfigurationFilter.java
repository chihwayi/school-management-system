package com.devtech.school_management_system.filter;

import com.devtech.school_management_system.service.SchoolServiceImpl;
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
            "/api/school/setup"
    );

    private static final List<String> AUTH_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register"
    );

    private final SchoolServiceImpl schoolService;

    @Autowired
    public SchoolConfigurationFilter(SchoolServiceImpl schoolService) {
        this.schoolService = schoolService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // If the request is for static resources or setup endpoints, proceed normally
        if (isSetupEndpoint(path) || isStaticResource(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if school is configured
        boolean isSchoolConfigured = schoolService.isSchoolConfigured();

        // If school is not configured and trying to access non-setup endpoints
        if (!isSchoolConfigured && !isSetupEndpoint(path)) {
            // If this is an auth endpoint, return error as JSON
            if (isAuthEndpoint(path)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\":\"School setup is required before authentication\",\"setupRequired\":true}");
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
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
                path.equals("/favicon.ico");
    }
}