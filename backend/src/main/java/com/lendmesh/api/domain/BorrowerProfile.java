package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * The simulated financial inputs a borrower supplies for one loan
 * application. This is the raw material the {@code RiskScoringEngine}
 * turns into a grade — every field here maps to a line in the scorecard,
 * so a lender can trace a grade back to the numbers that produced it.
 */
@Entity
@Table(name = "borrower_profile")
public class BorrowerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal annualIncome;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal existingMonthlyDebt;

    /** 0-100, simulated payment-history reliability. */
    @Column(nullable = false)
    private int onTimePaymentRate;

    /** Percentage of available revolving credit currently in use, 0-100. */
    @Column(nullable = false)
    private int creditUtilization;

    @Column(nullable = false)
    private int monthsOfCreditHistory;

    @Column(nullable = false)
    private int openDelinquencies;

    protected BorrowerProfile() {
    }

    public BorrowerProfile(String userId, BigDecimal annualIncome, BigDecimal existingMonthlyDebt,
                            int onTimePaymentRate, int creditUtilization,
                            int monthsOfCreditHistory, int openDelinquencies) {
        this.userId = userId;
        this.annualIncome = annualIncome;
        this.existingMonthlyDebt = existingMonthlyDebt;
        this.onTimePaymentRate = onTimePaymentRate;
        this.creditUtilization = creditUtilization;
        this.monthsOfCreditHistory = monthsOfCreditHistory;
        this.openDelinquencies = openDelinquencies;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public BigDecimal getExistingMonthlyDebt() {
        return existingMonthlyDebt;
    }

    public int getOnTimePaymentRate() {
        return onTimePaymentRate;
    }

    public int getCreditUtilization() {
        return creditUtilization;
    }

    public int getMonthsOfCreditHistory() {
        return monthsOfCreditHistory;
    }

    public int getOpenDelinquencies() {
        return openDelinquencies;
    }
}
