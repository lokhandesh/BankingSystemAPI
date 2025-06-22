CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(100) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_account FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);