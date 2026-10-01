CREATE TABLE financial_goals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    goal_type VARCHAR(30) NOT NULL,
    target_amount DECIMAL(19, 2) NOT NULL,
    current_amount DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    target_date DATE NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    notes VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_financial_goals
        PRIMARY KEY (id),

    CONSTRAINT fk_financial_goals_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_financial_goals_target_amount
        CHECK (target_amount > 0),

    CONSTRAINT chk_financial_goals_current_amount
        CHECK (current_amount >= 0)
);

CREATE INDEX idx_financial_goals_user_id
    ON financial_goals (user_id);

CREATE INDEX idx_financial_goals_user_status
    ON financial_goals (user_id, status);

CREATE INDEX idx_financial_goals_target_date
    ON financial_goals (target_date);


CREATE TABLE goal_contributions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    goal_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    contribution_date DATE NOT NULL,
    note VARCHAR(300) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_goal_contributions
        PRIMARY KEY (id),

    CONSTRAINT fk_goal_contributions_goal
        FOREIGN KEY (goal_id)
        REFERENCES financial_goals (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_goal_contributions_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_goal_contributions_goal_id
    ON goal_contributions (goal_id);

CREATE INDEX idx_goal_contributions_goal_date
    ON goal_contributions (
        goal_id,
        contribution_date
    );