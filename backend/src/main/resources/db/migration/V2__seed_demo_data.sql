-- Demo borrower/lender so a fresh environment isn't an empty marketplace.
-- Password for both is "password123" (BCrypt hash below).
INSERT INTO app_user (id, display_name, email, password_hash, created_at) VALUES
    ('demo-borrower', 'Amara Osei', 'amara@example.com', '$2a$10$W6PJJwZamddhrfiA4yhIIuRlPBR2fKYLJuzd/FecxhlMeK9dPTOGe', CURRENT_TIMESTAMP),
    ('demo-lender',   'Chen Wei',   'chen@example.com',   '$2a$10$W6PJJwZamddhrfiA4yhIIuRlPBR2fKYLJuzd/FecxhlMeK9dPTOGe', CURRENT_TIMESTAMP);

INSERT INTO app_user_role (user_id, role) VALUES
    ('demo-borrower', 'BORROWER'),
    ('demo-lender', 'LENDER');

INSERT INTO wallet (id, user_id, balance, version) VALUES
    ('demo-borrower-wallet', 'demo-borrower', 10000.00, 0),
    ('demo-lender-wallet', 'demo-lender', 25000.00, 0);

INSERT INTO borrower_profile (id, user_id, annual_income, existing_monthly_debt, on_time_payment_rate,
                               credit_utilization, months_of_credit_history, open_delinquencies) VALUES
    ('demo-profile-1', 'demo-borrower', 72000.00, 650.00, 94, 28, 60, 0);

INSERT INTO loan_listing (id, borrower_id, borrower_profile_id, purpose, requested_amount, funded_amount,
                           term_months, risk_grade, scorecard_total, interest_rate, status, listed_at) VALUES
    ('demo-loan-1', 'demo-borrower', 'demo-profile-1', 'Consolidating two credit cards',
     5000.00, 0.00, 24, 'B', 82, 0.0890, 'OPEN_FOR_FUNDING', CURRENT_TIMESTAMP);
