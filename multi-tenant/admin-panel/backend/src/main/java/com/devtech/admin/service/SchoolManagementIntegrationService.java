package com.devtech.admin.service;

import com.devtech.admin.entity.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Service
public class SchoolManagementIntegrationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String mainDatabaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    /**
     * Creates a new school database and initializes it with the school management system schema
     */
    public void createSchoolDatabase(School school) {
        try {
            // Create the school database
            String schoolDatabaseName = school.getDatabaseName();
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + schoolDatabaseName;
            
            jdbcTemplate.execute(createDatabaseSQL);
            
            // Initialize the school database with the school management system schema
            initializeSchoolDatabase(schoolDatabaseName, school);
            
            // Register the tenant with the school management system
            registerTenantWithSchoolManagementSystem(school);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create school database: " + e.getMessage(), e);
        }
    }

    /**
     * Initializes a school database with the school management system schema
     */
    private void initializeSchoolDatabase(String databaseName, School school) {
        try {
            // Create a new DataSource for the school database
            DriverManagerDataSource schoolDataSource = new DriverManagerDataSource();
            schoolDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            schoolDataSource.setUrl(mainDatabaseUrl.replace("/school_management_system_admin", "/" + databaseName));
            schoolDataSource.setUsername(databaseUsername);
            schoolDataSource.setPassword(databasePassword);

            JdbcTemplate schoolJdbcTemplate = new JdbcTemplate(schoolDataSource);

            // Execute the school management system initialization scripts
            executeSchoolInitScripts(schoolJdbcTemplate, school);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize school database: " + e.getMessage(), e);
        }
    }

    /**
     * Executes the school management system initialization scripts
     */
    private void executeSchoolInitScripts(JdbcTemplate schoolJdbcTemplate, School school) {
        try {
            // List of initialization scripts to execute for new databases
            // Skip migration scripts (02-add-receipt-number.sql) for new databases
            String[] initScripts = {
                "01-init.sql",
                "03-create-tables.sql",
                "04-seed-data.sql"
            };

            for (String script : initScripts) {
                try {
                    executeSQLScript(schoolJdbcTemplate, script, school);
                    System.out.println("Successfully executed script: " + script);
                } catch (Exception e) {
                    System.err.println("Failed to execute script " + script + ": " + e.getMessage());
                    // Continue with other scripts
                }
            }

            // Create school-specific configuration
            createSchoolConfiguration(schoolJdbcTemplate, school);

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute school init scripts: " + e.getMessage(), e);
        }
    }

    /**
     * Executes a SQL script from the resources
     */
    private void executeSQLScript(JdbcTemplate jdbcTemplate, String scriptName, School school) {
        try {
            // Read the script from resources
            String scriptContent = readResourceFile("/mysql/init/" + scriptName);
            
            // Replace placeholders with school-specific values
            scriptContent = replaceSchoolPlaceholders(scriptContent, school);
            
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
     * Creates school-specific configuration in the school database
     */
    private void createSchoolConfiguration(JdbcTemplate jdbcTemplate, School school) {
        try {
            // Insert school configuration into the schools table
            String insertSchoolSQL = """
                INSERT INTO schools (name, primary_color, secondary_color, contact_email, contact_phone, address, configured, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, 1, NOW(), NOW())
                """;
            
            jdbcTemplate.update(insertSchoolSQL,
                school.getName(),
                school.getPrimaryColor() != null ? school.getPrimaryColor() : "#3B82F6",
                school.getSecondaryColor() != null ? school.getSecondaryColor() : "#1E40AF",
                school.getAdminEmail(),
                school.getContactPhone(),
                school.getAddress()
            );

            // Create default admin user for the school
            String createAdminSQL = """
                INSERT INTO users (username, email, password, first_name, last_name, created_at) 
                VALUES (?, ?, ?, ?, ?, NOW())
                """;
            
            // Use BCrypt hash for 'admin123' password
            String adminPasswordHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
            
            jdbcTemplate.update(createAdminSQL,
                school.getAdminUsername(),
                school.getAdminEmail(),
                adminPasswordHash,
                "Admin",
                "User"
            );
            
            // Create admin role if roles table exists
            try {
                String createRoleSQL = "INSERT IGNORE INTO roles (name) VALUES ('ADMIN')";
                jdbcTemplate.update(createRoleSQL);
                
                // Get the admin user ID and role ID
                String getUserIdSQL = "SELECT id FROM users WHERE username = ?";
                Long userId = jdbcTemplate.queryForObject(getUserIdSQL, Long.class, school.getAdminUsername());
                
                String getRoleIdSQL = "SELECT id FROM roles WHERE name = 'ADMIN'";
                Long roleId = jdbcTemplate.queryForObject(getRoleIdSQL, Long.class);
                
                // Assign admin role to user
                String assignRoleSQL = "INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (?, ?)";
                jdbcTemplate.update(assignRoleSQL, userId, roleId);
                
            } catch (Exception e) {
                System.err.println("Failed to create admin role assignment: " + e.getMessage());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create school configuration: " + e.getMessage(), e);
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
     * Replaces placeholders in SQL scripts with school-specific values
     */
    private String replaceSchoolPlaceholders(String content, School school) {
        return content
            .replace("${SCHOOL_NAME}", school.getName())
            .replace("${SCHOOL_ADDRESS}", school.getAddress() != null ? school.getAddress() : "")
            .replace("${ADMIN_EMAIL}", school.getAdminEmail())
            .replace("${ADMIN_USERNAME}", school.getAdminUsername())
            .replace("${TIMEZONE}", school.getTimezone())
            .replace("${CURRENCY}", school.getCurrency())
            .replace("${LANGUAGE}", school.getLanguage())
            // Remove USE statements as we're already connected to the correct database
            .replaceAll("(?i)USE\s+[^;]+;", "")
            // Replace any references to the default database name
            .replace("school_management_system", school.getDatabaseName());
    }

    /**
     * Gets real statistics for a school from its database
     */
    public Map<String, Object> getRealSchoolStats(String schoolId) {
        try {
            School school = getSchoolById(schoolId);
            String databaseName = school.getDatabaseName();
            
            // Create connection to school database
            DriverManagerDataSource schoolDataSource = new DriverManagerDataSource();
            schoolDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            schoolDataSource.setUrl(mainDatabaseUrl.replace("/school_management_system_admin", "/" + databaseName));
            schoolDataSource.setUsername(databaseUsername);
            schoolDataSource.setPassword(databasePassword);

            JdbcTemplate schoolJdbcTemplate = new JdbcTemplate(schoolDataSource);

            // Get real statistics with error handling for each table
            Integer totalStudents = 0;
            Integer totalTeachers = 0;
            Integer totalClasses = 0;
            Integer totalUsers = 0;
            
            try {
                totalStudents = schoolJdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
            } catch (Exception e) {
                System.err.println("Error counting students: " + e.getMessage());
            }
            
            try {
                totalTeachers = schoolJdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);
            } catch (Exception e) {
                System.err.println("Error counting teachers: " + e.getMessage());
            }
            
            try {
                totalClasses = schoolJdbcTemplate.queryForObject("SELECT COUNT(*) FROM class_groups", Integer.class);
            } catch (Exception e) {
                System.err.println("Error counting classes: " + e.getMessage());
            }
            
            try {
                totalUsers = schoolJdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            } catch (Exception e) {
                System.err.println("Error counting users: " + e.getMessage());
            }

            return Map.of(
                "totalStudents", totalStudents != null ? totalStudents : 0,
                "totalTeachers", totalTeachers != null ? totalTeachers : 0,
                "totalClasses", totalClasses != null ? totalClasses : 0,
                "totalUsers", totalUsers != null ? totalUsers : 0,
                "schoolName", school.getName(),
                "status", school.getStatus(),
                "planType", school.getPlanType()
            );

        } catch (Exception e) {
            // Return default stats if there's an error
            return Map.of(
                "totalStudents", 0,
                "totalTeachers", 0,
                "totalClasses", 0,
                "totalUsers", 0,
                "error", "Unable to fetch real statistics: " + e.getMessage()
            );
        }
    }

    /**
     * Gets real user data for a school from its database
     */
    public List<Map<String, Object>> getRealSchoolUsers(String schoolId) {
        try {
            School school = getSchoolById(schoolId);
            String databaseName = school.getDatabaseName();
            
            // Create connection to school database
            DriverManagerDataSource schoolDataSource = new DriverManagerDataSource();
            schoolDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            schoolDataSource.setUrl(mainDatabaseUrl.replace("/school_management_system_admin", "/" + databaseName));
            schoolDataSource.setUsername(databaseUsername);
            schoolDataSource.setPassword(databasePassword);

            JdbcTemplate schoolJdbcTemplate = new JdbcTemplate(schoolDataSource);

            // Get real user data with error handling
            String userQuery = """
                SELECT id, username, email, role, status, created_at 
                FROM users 
                ORDER BY created_at DESC 
                LIMIT 10
                """;
            
            return schoolJdbcTemplate.queryForList(userQuery);

        } catch (Exception e) {
            System.err.println("Error fetching school users: " + e.getMessage());
            // Return empty list if there's an error
            return List.of();
        }
    }

    /**
     * Deletes a school database when a school is deleted
     */
    public void deleteSchoolDatabase(String databaseName) {
        try {
            String dropDatabaseSQL = "DROP DATABASE IF EXISTS " + databaseName;
            jdbcTemplate.execute(dropDatabaseSQL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete school database: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a tenant with the school management system
     */
    private void registerTenantWithSchoolManagementSystem(School school) {
        try {
            // Create HTTP client to call school management system
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            
            // Prepare the request body
            String requestBody = String.format(
                "{\"tenantId\":\"%s\",\"databaseName\":\"%s\"}",
                school.getSubdomain(),
                school.getDatabaseName()
            );
            
            // Create the request
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://school-backend:8080/api/tenant/register"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(java.time.Duration.ofSeconds(10))
                .build();
            
            // Send the request
            java.net.http.HttpResponse<String> response = client.send(request, 
                java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Successfully registered tenant: " + school.getSubdomain() + 
                    " with database: " + school.getDatabaseName());
            } else {
                System.err.println("Failed to register tenant (HTTP " + response.statusCode() + "): " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("Failed to register tenant with school management system: " + e.getMessage());
            // Don't throw the exception to avoid failing the school creation
        }
    }

    /**
     * Creates a user in a specific school's database
     */
    public Map<String, Object> createUserInSchoolDatabase(String schoolId, Map<String, Object> userRequest) {
        try {
            School school = getSchoolById(schoolId);
            String databaseName = school.getDatabaseName();
            
            // Create connection to school database
            DriverManagerDataSource schoolDataSource = new DriverManagerDataSource();
            schoolDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            schoolDataSource.setUrl(mainDatabaseUrl.replace("/school_management_system_admin", "/" + databaseName));
            schoolDataSource.setUsername(databaseUsername);
            schoolDataSource.setPassword(databasePassword);

            JdbcTemplate schoolJdbcTemplate = new JdbcTemplate(schoolDataSource);

            // Create user in school database
            String insertUserSQL = """
                INSERT INTO users (username, email, password, first_name, last_name, created_at) 
                VALUES (?, ?, ?, ?, ?, NOW())
                """;
            
            // Use BCrypt hash for the provided password (you should hash this properly)
            String passwordHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa"; // Default: admin123
            
            schoolJdbcTemplate.update(insertUserSQL,
                userRequest.get("username"),
                userRequest.get("email"),
                passwordHash,
                userRequest.get("username"), // Use username as first name for now
                "User"
            );
            
            // Get the created user ID
            String getUserIdSQL = "SELECT id FROM users WHERE username = ?";
            Long userId = schoolJdbcTemplate.queryForObject(getUserIdSQL, Long.class, userRequest.get("username"));
            
            // Create role if it doesn't exist
            String role = (String) userRequest.get("role");
            String createRoleSQL = "INSERT IGNORE INTO roles (name) VALUES (?)";
            schoolJdbcTemplate.update(createRoleSQL, role);
            
            // Get role ID
            String getRoleIdSQL = "SELECT id FROM roles WHERE name = ?";
            Long roleId = schoolJdbcTemplate.queryForObject(getRoleIdSQL, Long.class, role);
            
            // Assign role to user
            String assignRoleSQL = "INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (?, ?)";
            schoolJdbcTemplate.update(assignRoleSQL, userId, roleId);
            
            return Map.of(
                "id", userId,
                "username", userRequest.get("username"),
                "email", userRequest.get("email"),
                "role", role,
                "status", "ACTIVE",
                "schoolId", schoolId,
                "schoolName", school.getName(),
                "createdAt", java.time.Instant.now().toString()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in school database: " + e.getMessage(), e);
        }
    }



    /**
     * Gets all users from all school databases
     */
    public List<Map<String, Object>> getAllUsersFromAllSchools() {
        List<Map<String, Object>> allUsers = new java.util.ArrayList<>();
        
        try {
            // Get all schools from admin database
            String schoolsQuery = "SELECT school_id, name, database_name FROM schools WHERE status = 'active'";
            List<Map<String, Object>> schools = jdbcTemplate.queryForList(schoolsQuery);
            System.out.println("Found " + schools.size() + " active schools");
            
            for (Map<String, Object> school : schools) {
                String databaseName = (String) school.get("database_name");
                String schoolName = (String) school.get("name");
                String schoolId = (String) school.get("school_id");
                
                System.out.println("Fetching users from: " + schoolName + " (" + databaseName + ")");
                
                try {
                    // Query to get users with their primary roles and real names
                    String usersQuery = "SELECT u.id, u.username, u.email, u.first_name, u.last_name, u.created_at, " +
                        "COALESCE(CASE WHEN r.name LIKE 'ROLE_%' THEN REPLACE(r.name, 'ROLE_', '') ELSE r.name END, 'USER') as role " +
                        "FROM " + databaseName + ".users u " +
                        "LEFT JOIN " + databaseName + ".user_roles ur ON u.id = ur.user_id " +
                        "LEFT JOIN " + databaseName + ".roles r ON ur.role_id = r.id " +
                        "GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.created_at " +
                        "ORDER BY u.created_at DESC LIMIT 10";
                    List<Map<String, Object>> schoolUsers = jdbcTemplate.queryForList(usersQuery);
                    
                    System.out.println("Found " + schoolUsers.size() + " users in " + schoolName);
                    
                    // Add school info to each user
                    for (Map<String, Object> user : schoolUsers) {
                        Map<String, Object> userWithSchool = new java.util.HashMap<>(user);
                        userWithSchool.put("schoolId", schoolId);
                        userWithSchool.put("schoolName", schoolName);
                        userWithSchool.put("status", "ACTIVE");
                        
                        // Build full name from first_name and last_name
                        String firstName = (String) user.get("first_name");
                        String lastName = (String) user.get("last_name");
                        String fullName = "";
                        if (firstName != null && lastName != null) {
                            fullName = firstName + " " + lastName;
                        } else if (firstName != null) {
                            fullName = firstName;
                        } else {
                            fullName = (String) user.get("username");
                        }
                        userWithSchool.put("fullName", fullName);
                        
                        System.out.println("User: " + fullName + ", Role: " + user.get("role"));
                        
                        allUsers.add(userWithSchool);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error fetching users from " + schoolName + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in getAllUsersFromAllSchools: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Total users found: " + allUsers.size());
        return allUsers;
    }

    /**
     * Execute direct SQL query
     */
    public List<Map<String, Object>> executeQuery(String query) {
        try {
            return jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a school by ID (helper method)
     */
    private School getSchoolById(String schoolId) {
        try {
            String query = "SELECT * FROM schools WHERE school_id = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(query, schoolId);
            
            School school = new School();
            school.setSchoolId((String) result.get("school_id"));
            school.setName((String) result.get("name"));
            school.setSubdomain((String) result.get("subdomain"));
            school.setDomain((String) result.get("domain"));
            school.setDatabaseName((String) result.get("database_name"));
            school.setPlanType((String) result.get("plan_type"));
            school.setAdminEmail((String) result.get("admin_email"));
            school.setAdminUsername((String) result.get("admin_username"));
            school.setStatus((String) result.get("status"));
            school.setContactPhone((String) result.get("contact_phone"));
            school.setAddress((String) result.get("address"));
            school.setTimezone((String) result.get("timezone"));
            school.setCurrency((String) result.get("currency"));
            school.setLanguage((String) result.get("language"));
            
            return school;
        } catch (Exception e) {
            throw new RuntimeException("School not found: " + schoolId, e);
        }
    }
}
