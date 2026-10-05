CREATE TABLE portfolio_assets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    asset_name VARCHAR(150) NOT NULL,
    symbol VARCHAR(30) NULL,
    asset_type VARCHAR(30) NOT NULL,
    quantity DECIMAL(19, 6) NOT NULL,
    average_purchase_price DECIMAL(19, 4) NOT NULL,
    current_price DECIMAL(19, 4) NOT NULL,
    investment_date DATE NOT NULL,
    institution_name VARCHAR(150) NULL,
    account_reference VARCHAR(100) NULL,
    maturity_date DATE NULL,
    interest_rate DECIMAL(7, 4) NULL,
    notes VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_portfolio_assets
        PRIMARY KEY (id),

    CONSTRAINT fk_portfolio_assets_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_portfolio_assets_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_portfolio_assets_purchase_price
        CHECK (average_purchase_price >= 0),

    CONSTRAINT chk_portfolio_assets_current_price
        CHECK (current_price >= 0),

    CONSTRAINT chk_portfolio_assets_interest_rate
        CHECK (
            interest_rate IS NULL
            OR interest_rate >= 0
        )
);

CREATE INDEX idx_portfolio_assets_user_id
    ON portfolio_assets (user_id);

CREATE INDEX idx_portfolio_assets_user_type
    ON portfolio_assets (
        user_id,
        asset_type
    );

CREATE INDEX idx_portfolio_assets_investment_date
    ON portfolio_assets (investment_date);