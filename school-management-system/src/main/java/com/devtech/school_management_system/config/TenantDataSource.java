package com.devtech.school_management_system.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TenantDataSource extends AbstractRoutingDataSource {
    
    private final Map<String, DataSource> dataSources = new HashMap<>();
    private final DataSource defaultDataSource;
    private final String baseUrl;
    private final String username;
    private final String password;
    
    public TenantDataSource(DataSource defaultDataSource, String baseUrl, String username, String password) {
        this.defaultDataSource = defaultDataSource;
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        setDefaultTargetDataSource(defaultDataSource);
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getCurrentTenant();
        String database = TenantContext.getCurrentDatabase();
        
        System.out.println("TenantDataSource - Current tenant: " + tenant + ", Database: " + database);
        
        if (tenant == null || "default".equals(tenant)) {
            System.out.println("TenantDataSource - Using default database");
            return "default";
        }
        
        // Check if we have this data source cached
        if (!dataSources.containsKey(tenant)) {
            System.out.println("TenantDataSource - Creating new data source for tenant: " + tenant);
            DataSource tenantDataSource = createTenantDataSource(database);
            dataSources.put(tenant, tenantDataSource);
            
            // Update target data sources
            Map<Object, Object> targetDataSources = new HashMap<>();
            targetDataSources.put("default", defaultDataSource);
            targetDataSources.putAll(dataSources);
            setTargetDataSources(targetDataSources);
            afterPropertiesSet();
        }
        
        System.out.println("TenantDataSource - Using tenant data source: " + tenant);
        return tenant;
    }
    
    private DataSource createTenantDataSource(String databaseName) {
        HikariDataSource dataSource = new HikariDataSource();
        
        // Construct the database URL
        String databaseUrl = baseUrl.replace("/school_management_system", "/" + databaseName);
        System.out.println("TenantDataSource - Creating connection to: " + databaseUrl);
        
        dataSource.setJdbcUrl(databaseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Connection pool settings
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(20000);
        dataSource.setIdleTimeout(300000);
        
        return dataSource;
    }
    
    public void addDataSource(String tenant, DataSource dataSource) {
        dataSources.put(tenant, dataSource);
    }
    
    public DataSource getDataSource(String tenant) {
        return dataSources.getOrDefault(tenant, defaultDataSource);
    }
    
    public void removeDataSource(String tenant) {
        dataSources.remove(tenant);
    }
}
