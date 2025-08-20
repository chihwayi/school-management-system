-- Add receipt_number column to fee_payments table
ALTER TABLE fee_payments ADD COLUMN receipt_number VARCHAR(20) UNIQUE;

-- Create index for better performance
CREATE INDEX idx_fee_payments_receipt_number ON fee_payments(receipt_number);
