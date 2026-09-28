CREATE TABLE budgets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    budget_month DATE NOT NULL,
    category VARCHAR(40) NULL,
    alert_threshold DECIMAL(5, 2) NOT NULL DEFAULT 80.00,
    notes VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_budgets
        PRIMARY KEY (id),

    CONSTRAINT fk_budgets_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_budgets_amount_positive
        CHECK (amount > 0),

    CONSTRAINT chk_budgets_alert_threshold
        CHECK (
            alert_threshold > 0
            AND alert_threshold <= 100
        )
);

CREATE INDEX idx_budgets_user_id
    ON budgets (user_id);

CREATE INDEX idx_budgets_user_month
    ON budgets (user_id, budget_month);

CREATE INDEX idx_budgets_user_category_month
    ON budgets (user_id, category, budget_month);