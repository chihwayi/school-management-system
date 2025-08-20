-- Add receipt_number column to fee_payments table if it doesn't exist
ALTER TABLE fee_payments ADD COLUMN IF NOT EXISTS receipt_number VARCHAR(20) UNIQUE;
