-- Create mock_invoices table in voucher_db
USE voucher_db;

CREATE TABLE IF NOT EXISTS mock_invoices (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(255) NOT NULL,
    merchant_id CHAR(36) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_created_at (created_at)
);

-- Insert sample data
INSERT INTO mock_invoices (id, title, merchant_id, amount) VALUES
(UUID(), 'Tiền điện tháng 12', UUID(), 150000.00),
(UUID(), 'Hóa đơn trà sữa', UUID(), 45000.00),
(UUID(), 'Tiền nước tháng 12', UUID(), 80000.00),
(UUID(), 'Hóa đơn cà phê', UUID(), 35000.00),
(UUID(), 'Tiền internet tháng 12', UUID(), 200000.00);