package com.devtech.school_management_system.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MultiTenantConfig implements WebMvcConfigurer {

    @PostConstruct
    public void init() {
        System.out.println("=== MultiTenantConfig LOADED ===");
    }

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println("=== MultiTenantConfig - Creating multi-tenant data source ===");
        // Create the default data source
        HikariDataSource defaultDataSource = new HikariDataSource();
        defaultDataSource.setJdbcUrl(databaseUrl);
        defaultDataSource.setUsername(databaseUsername);
        defaultDataSource.setPassword(databasePassword);
        defaultDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Create the tenant-aware data source
        TenantDataSource tenantDataSource = new TenantDataSource(
            defaultDataSource, 
            databaseUrl, 
            databaseUsername, 
            databasePassword
        );
        
        // Set the target data sources
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("default", defaultDataSource);
        tenantDataSource.setTargetDataSources(targetDataSources);
        tenantDataSource.afterPropertiesSet();
        
        return tenantDataSource;
    }

    @Bean
    public TenantInterceptor tenantInterceptor() {
        return new TenantInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("=== MultiTenantConfig - Registering TenantInterceptor ===");
        registry.addInterceptor(tenantInterceptor()).addPathPatterns("/api/**");
        System.out.println("=== MultiTenantConfig - TenantInterceptor registered successfully ===");
    }
}
