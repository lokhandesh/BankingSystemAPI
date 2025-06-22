CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    account_id BIGINT,
    CONSTRAINT fk_account_transaction FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE SET NULL
);