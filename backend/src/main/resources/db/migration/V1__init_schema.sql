CREATE TABLE app_user (
    id             VARCHAR(36) PRIMARY KEY,
    display_name   VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    CONSTRAINT uq_app_user_email UNIQUE (email)
);

CREATE TABLE app_user_role (
    user_id VARCHAR(36) NOT NULL REFERENCES app_user(id),
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE wallet (
    id       VARCHAR(36) PRIMARY KEY,
    user_id  VARCHAR(36) NOT NULL,
    balance  DECIMAL(19,4) NOT NULL,
    version  BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_wallet_user UNIQUE (user_id)
);

CREATE TABLE ledger_entry (
    id              VARCHAR(36) PRIMARY KEY,
    wallet_id       VARCHAR(36) NOT NULL,
    type            VARCHAR(32) NOT NULL,
    amount          DECIMAL(19,4) NOT NULL,
    balance_after   DECIMAL(19,4) NOT NULL,
    related_loan_id VARCHAR(36),
    memo            VARCHAR(500) NOT NULL,
    occurred_at     TIMESTAMP NOT NULL
);
CREATE INDEX idx_ledger_wallet ON ledger_entry(wallet_id);

CREATE TABLE borrower_profile (
    id                       VARCHAR(36) PRIMARY KEY,
    user_id                  VARCHAR(36) NOT NULL,
    annual_income            DECIMAL(19,2) NOT NULL,
    existing_monthly_debt    DECIMAL(19,2) NOT NULL,
    on_time_payment_rate     INT NOT NULL,
    credit_utilization       INT NOT NULL,
    months_of_credit_history INT NOT NULL,
    open_delinquencies       INT NOT NULL
);

CREATE TABLE loan_listing (
    id                  VARCHAR(36) PRIMARY KEY,
    borrower_id         VARCHAR(36) NOT NULL,
    borrower_profile_id VARCHAR(36) NOT NULL,
    purpose             VARCHAR(200) NOT NULL,
    requested_amount    DECIMAL(19,2) NOT NULL,
    funded_amount       DECIMAL(19,2) NOT NULL,
    term_months         INT NOT NULL,
    risk_grade          VARCHAR(8) NOT NULL,
    scorecard_total     INT NOT NULL,
    interest_rate       DECIMAL(6,4) NOT NULL,
    status              VARCHAR(32) NOT NULL,
    listed_at           TIMESTAMP NOT NULL,
    funded_at           TIMESTAMP,
    version             BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_loan_status ON loan_listing(status);
CREATE INDEX idx_loan_borrower ON loan_listing(borrower_id);

CREATE TABLE loan_note (
    id                  VARCHAR(36) PRIMARY KEY,
    loan_listing_id     VARCHAR(36) NOT NULL,
    lender_id           VARCHAR(36) NOT NULL,
    principal_committed DECIMAL(19,2) NOT NULL,
    principal_repaid    DECIMAL(19,2) NOT NULL,
    interest_repaid     DECIMAL(19,2) NOT NULL,
    funded_at           TIMESTAMP NOT NULL
);
CREATE INDEX idx_note_listing ON loan_note(loan_listing_id);
CREATE INDEX idx_note_lender ON loan_note(lender_id);

CREATE TABLE installment (
    id                       VARCHAR(36) PRIMARY KEY,
    loan_listing_id          VARCHAR(36) NOT NULL,
    period_number            INT NOT NULL,
    principal_due            DECIMAL(19,4) NOT NULL,
    interest_due             DECIMAL(19,4) NOT NULL,
    remaining_balance_after  DECIMAL(19,4) NOT NULL,
    status                   VARCHAR(16) NOT NULL,
    settled_at               TIMESTAMP
);
CREATE INDEX idx_installment_listing ON installment(loan_listing_id);
CREATE INDEX idx_installment_status ON installment(status);
