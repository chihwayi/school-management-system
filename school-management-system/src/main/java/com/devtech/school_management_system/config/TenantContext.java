package com.devtech.school_management_system.config;

public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<String> currentDatabase = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
        if (tenant != null && !"default".equals(tenant)) {
            // Ensure proper database naming: final_test -> school_final_test
            String dbName = tenant.startsWith("school_") ? tenant : "school_" + tenant;
            currentDatabase.set(dbName);
            System.out.println("TenantContext - Set tenant: " + tenant + " -> database: " + dbName);
        } else {
            currentDatabase.set("school_management_system");
            System.out.println("TenantContext - Using default database: school_management_system");
        }
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static String getCurrentDatabase() {
        return currentDatabase.get();
    }

    public static void clear() {
        currentTenant.remove();
        currentDatabase.remove();
    }
}
