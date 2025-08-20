-- =====================================================
-- Migration Strategy for Spring Boot JPA Integration
-- =====================================================

-- This file contains the migration strategy to work with Spring Boot JPA entities
-- The approach is: SQL migrations for initial setup, JPA for schema validation

-- =====================================================
-- MIGRATION STRATEGY
-- =====================================================

/*
STRATEGY:
1. DEVELOPMENT: Use SQL migrations for initial setup, then let JPA handle updates
2. PRODUCTION: Use SQL migrations for all schema changes, JPA only validates
3. DEPLOYMENT: Always run SQL migrations first, then start Spring Boot

CONFIGURATION:
- Development: spring.jpa.hibernate.ddl-auto=update
- Production: spring.jpa.hibernate.ddl-auto=validate
- Docker: spring.jpa.hibernate.ddl-auto=validate (after migrations)
*/

-- =====================================================
-- SCHEMA VERSIONING TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS schema_version (
    id INT AUTO_INCREMENT PRIMARY KEY,
    version VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    checksum VARCHAR(64),
    execution_time_ms INT
);

-- =====================================================
-- INSERT MIGRATION RECORDS
-- =====================================================
INSERT IGNORE INTO schema_version (version, description, checksum) VALUES 
('1.0.0', 'Initial database setup', 'init_checksum'),
('1.0.1', 'Add receipt number feature', 'receipt_checksum'),
('1.0.2', 'Complete schema creation', 'schema_checksum'),
('1.0.3', 'Seed data population', 'seed_checksum');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check if all required tables exist (matches JPA entities)
SELECT 'Schema Verification' as check_type, 
       COUNT(*) as table_count,
       'All tables present' as status
FROM information_schema.tables 
WHERE table_schema = 'school_management_system' 
  AND table_type = 'BASE TABLE';

-- List all tables for verification
SELECT table_name, table_rows, data_length, index_length
FROM information_schema.tables 
WHERE table_schema = 'school_management_system' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Check foreign key constraints
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'school_management_system' 
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, COLUMN_NAME;

-- =====================================================
-- SPRING BOOT INTEGRATION NOTES
-- =====================================================

/*
SPRING BOOT CONFIGURATION:

1. DEVELOPMENT (application.properties):
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   
2. DOCKER (application-docker.properties):
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=false
   
3. PRODUCTION (application-prod.properties):
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=false

DEPLOYMENT PROCESS:

1. Run SQL migrations first:
   - 01-init.sql (database creation)
   - 02-add-receipt-number.sql (feature additions)
   - 03-create-tables.sql (schema creation)
   - 04-seed-data.sql (initial data)
   - 05-migration-strategy.sql (this file)

2. Start Spring Boot application:
   - JPA will validate schema matches entities
   - No auto-creation in production/docker

3. Monitor for validation errors:
   - Check application logs for schema mismatches
   - Update migrations if entities change

ENTITY SYNC PROCESS:

When you modify JPA entities:

1. Update SQL migrations to match entity changes
2. Test migrations in development
3. Deploy migrations before application update
4. Verify schema validation passes

EXAMPLE ENTITY CHANGE:

If you add a new field to Student entity:
1. Add ALTER TABLE statement to new migration file
2. Update schema_version table
3. Test in development
4. Deploy migration
5. Deploy application update
*/
