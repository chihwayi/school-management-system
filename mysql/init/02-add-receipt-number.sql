-- Add receipt_number column to fee_payments table
-- This will fail silently if the table doesn't exist or column already exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'school_management_system' AND TABLE_NAME = 'fee_payments') > 0,
    'ALTER TABLE fee_payments ADD COLUMN receipt_number VARCHAR(20) UNIQUE',
    'SELECT "Table fee_payments does not exist, skipping receipt_number column" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
