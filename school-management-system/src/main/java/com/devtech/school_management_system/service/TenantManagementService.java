package com.devtech.school_management_system.service;

import com.devtech.school_management_system.config.TenantDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class TenantManagementService {

    @Autowired
    private TenantDataSource tenantDataSource;

    @Value("${spring.datasource.url}")
    private String baseDatabaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    /**
     * Creates a new tenant database and adds it to the data source pool
     */
    public void createTenantDatabase(String tenantId, String databaseName) {
        try {
            // Create the database
            createDatabase(databaseName);
            
            // Initialize the database with schema
            initializeDatabase(databaseName);
            
            // Create and add the data source for this tenant
            DataSource tenantDataSource = createTenantDataSource(databaseName);
            this.tenantDataSource.addDataSource(tenantId, tenantDataSource);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tenant database: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new database
     */
    private void createDatabase(String databaseName) {
        // Use the base database URL to connect to MySQL server
        String serverUrl = baseDatabaseUrl.substring(0, baseDatabaseUrl.lastIndexOf("/"));
        
        HikariDataSource serverDataSource = new HikariDataSource();
        serverDataSource.setJdbcUrl(serverUrl);
        serverDataSource.setUsername(databaseUsername);
        serverDataSource.setPassword(databasePassword);
        serverDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(serverDataSource);
        
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        jdbcTemplate.execute(createDatabaseSQL);
        
        serverDataSource.close();
    }

    /**
     * Initializes a database with the school management system schema
     */
    private void initializeDatabase(String databaseName) {
        String databaseUrl = baseDatabaseUrl.replace("/school_management_system", "/" + databaseName);
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        // Execute initialization scripts
        executeInitScripts(jdbcTemplate);
        
        dataSource.close();
    }

    /**
     * Creates a data source for a specific tenant
     */
    private DataSource createTenantDataSource(String databaseName) {
        String databaseUrl = baseDatabaseUrl.replace("/school_management_system", "/" + databaseName);
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Configure connection pool
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(20000);
        dataSource.setIdleTimeout(300000);
        
        return dataSource;
    }

    /**
     * Executes initialization scripts for a new tenant database
     */
    private void executeInitScripts(JdbcTemplate jdbcTemplate) {
        // List of initialization scripts to execute
        String[] initScripts = {
            "01-init.sql",
            "02-add-receipt-number.sql",
            "03-create-tables.sql",
            "04-seed-data.sql"
        };

        for (String script : initScripts) {
            try {
                executeSQLScript(jdbcTemplate, script);
            } catch (Exception e) {
                // Log the error but continue with other scripts
                System.err.println("Failed to execute script " + script + ": " + e.getMessage());
            }
        }
        
        // Execute setup fields migration
        try {
            executeSQLScript(jdbcTemplate, "06-add-setup-fields.sql");
        } catch (Exception e) {
            System.err.println("Failed to execute setup fields migration: " + e.getMessage());
        }
    }

    /**
     * Executes a SQL script from the resources
     */
    private void executeSQLScript(JdbcTemplate jdbcTemplate, String scriptName) {
        try {
            // Read the script from resources
            String scriptContent = readResourceFile("/mysql/init/" + scriptName);
            
            // Split and execute the script
            String[] statements = scriptContent.split(";");
            
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    jdbcTemplate.execute(statement);
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute script " + scriptName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Reads a resource file from the classpath
     */
    private String readResourceFile(String resourcePath) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream(resourcePath)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource file: " + resourcePath, e);
        }
    }

    /**
     * Removes a tenant database from the pool
     */
    public void removeTenantDatabase(String tenantId) {
        tenantDataSource.removeDataSource(tenantId);
    }

    /**
     * Gets the current tenant ID
     */
    public String getCurrentTenant() {
        return com.devtech.school_management_system.config.TenantContext.getCurrentTenant();
    }

        /**
     * Checks if a tenant database exists and configures the school if it does
     */
    public boolean checkAndConfigureTenantDatabase() {
        try {
            System.out.println("=== TenantManagementService.checkAndConfigureTenantDatabase() called ===");
            
            // Get the current tenant from context
            String currentTenant = getCurrentTenant();
            System.out.println("Current tenant from context: " + currentTenant);

            if (currentTenant == null || "default".equals(currentTenant)) {
                System.out.println("No tenant found or default tenant, using test-school");
                currentTenant = "test-school"; // Use the tenant we created
            }

            // Check if the tenant database exists
            String databaseName = "school_" + currentTenant;
            System.out.println("Checking if database exists: " + databaseName);

            if (databaseExists(databaseName)) {
                System.out.println("Database exists, configuring school");
                // Database exists, configure the school
                configureSchoolFromTenantDatabase(currentTenant, databaseName);
                return true;
            } else {
                System.out.println("Database does not exist: " + databaseName);
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error checking tenant database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a database exists
     */
    private boolean databaseExists(String databaseName) {
        try {
            // Use the base database URL to connect to MySQL server
            String serverUrl = baseDatabaseUrl.substring(0, baseDatabaseUrl.lastIndexOf("/"));
            
            HikariDataSource serverDataSource = new HikariDataSource();
            serverDataSource.setJdbcUrl(serverUrl);
            serverDataSource.setUsername(databaseUsername);
            serverDataSource.setPassword(databasePassword);
            serverDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            JdbcTemplate jdbcTemplate = new JdbcTemplate(serverDataSource);
            
            String checkDatabaseSQL = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
            String result = jdbcTemplate.queryForObject(checkDatabaseSQL, String.class, databaseName);
            
            serverDataSource.close();
            
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Configures the school from an existing tenant database
     */
    private void configureSchoolFromTenantDatabase(String tenantId, String databaseName) {
        try {
            // Create a data source for this tenant
            DataSource tenantDataSource = createTenantDataSource(databaseName);
            this.tenantDataSource.addDataSource(tenantId, tenantDataSource);
            
            // Get school configuration from the tenant database
            String databaseUrl = baseDatabaseUrl.replace("/school_management_system", "/" + databaseName);
            
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(databaseUrl);
            dataSource.setUsername(databaseUsername);
            dataSource.setPassword(databasePassword);
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            // Check if school configuration exists in the tenant database
            String checkSchoolSQL = "SELECT COUNT(*) FROM schools WHERE configured = 1";
            Integer count = jdbcTemplate.queryForObject(checkSchoolSQL, Integer.class);
            
            if (count != null && count > 0) {
                // School is already configured in the tenant database
                System.out.println("School is already configured for tenant: " + tenantId);
            } else {
                // Create basic school configuration (requires user setup)
                String insertSchoolSQL = """
                    INSERT INTO schools (name, primary_color, secondary_color, contact_email, configured, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, 0, NOW(), NOW())
                    """;
                
                jdbcTemplate.update(insertSchoolSQL,
                    "School " + tenantId,
                    "#3B82F6",
                    "#1E40AF",
                    "admin@" + tenantId + ".com"
                );
                
                System.out.println("Basic school config created for tenant: " + tenantId + " - requires user setup");
            }
            
            dataSource.close();
            
        } catch (Exception e) {
            System.err.println("Error configuring school from tenant database: " + e.getMessage());
        }
    }
}
