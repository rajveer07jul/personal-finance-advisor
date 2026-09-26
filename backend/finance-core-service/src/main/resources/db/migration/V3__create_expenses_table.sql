CREATE TABLE expenses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    amount DECIMAL(19, 2) NOT NULL,
    category VARCHAR(40) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    expense_date DATE NOT NULL,
    merchant_name VARCHAR(100) NULL,
    notes VARCHAR(1000) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_expenses
        PRIMARY KEY (id),

    CONSTRAINT fk_expenses_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_expenses_amount_positive
        CHECK (amount > 0)
);

CREATE INDEX idx_expenses_user_id
    ON expenses (user_id);

CREATE INDEX idx_expenses_user_date
    ON expenses (user_id, expense_date);

CREATE INDEX idx_expenses_user_category
    ON expenses (user_id, category);