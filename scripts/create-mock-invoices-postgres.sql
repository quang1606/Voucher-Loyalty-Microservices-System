-- Create mock_invoices table in PostgreSQL
CREATE TABLE IF NOT EXISTS mock_invoices (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    name_store VARCHAR(255) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_mock_invoices_name_store ON mock_invoices(name_store);
CREATE INDEX IF NOT EXISTS idx_mock_invoices_created_at ON mock_invoices(created_at);

-- Insert sample data
INSERT INTO mock_invoices (title, name_store, amount) VALUES
('Tiền điện tháng 12', 'Công ty Điện lực Hà Nội', 150000.00),
('Hóa đơn trà sữa', 'Gong Cha', 45000.00),
('Tiền nước tháng 12', 'Công ty Cấp nước Hà Nội', 80000.00),
('Hóa đơn cà phê', 'Highlands Coffee', 35000.00),
('Tiền internet tháng 12', 'Viettel', 200000.00),
('Hóa đơn bánh mì', 'Bánh mì Phượng', 25000.00),
('Tiền gas tháng 12', 'Petrolimex Gas', 120000.00),
('Hóa đơn pizza', 'Pizza Hut', 180000.00),
('Tiền điện thoại tháng 12', 'Mobifone', 300000.00),
('Hóa đơn siêu thị', 'Vinmart', 450000.00),
('Hóa đơn xăng dầu', 'Petrolimex', 500000.00),
('Tiền thuê nhà tháng 12', 'Chung cư Times City', 8000000.00),
('Hóa đơn KFC', 'KFC', 120000.00),
('Hóa đơn Lotte Mart', 'Lotte Mart', 320000.00),
('Tiền bảo hiểm xe', 'Bảo Việt', 1200000.00)
ON CONFLICT DO NOTHING;