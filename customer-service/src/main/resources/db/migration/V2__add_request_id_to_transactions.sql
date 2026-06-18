ALTER TABLE transactions ADD COLUMN request_id VARCHAR(64);
CREATE INDEX idx_transactions_request_id ON transactions(request_id);
