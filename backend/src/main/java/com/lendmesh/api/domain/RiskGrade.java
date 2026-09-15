package com.lendmesh.api.domain;

import java.math.BigDecimal;

/**
 * Letter grade a loan listing is assigned by the scorecard, coarsest-first.
 * Each grade carries the base interest rate the marketplace offers lenders
 * for that risk band, and the per-period default probability the simulation
 * clock uses when deciding whether a borrower misses a payment.
 */
public enum RiskGrade {

    A(new BigDecimal("0.0650"), new BigDecimal("0.0040")),
    B(new BigDecimal("0.0890"), new BigDecimal("0.0090")),
    C(new BigDecimal("0.1150"), new BigDecimal("0.0170")),
    D(new BigDecimal("0.1420"), new BigDecimal("0.0290")),
    E(new BigDecimal("0.1780"), new BigDecimal("0.0480")),
    F(new BigDecimal("0.2250"), new BigDecimal("0.0790")),
    G(new BigDecimal("0.2890"), new BigDecimal("0.1250"));

    private final BigDecimal baseAnnualRate;
    private final BigDecimal defaultProbabilityPerPeriod;

    RiskGrade(BigDecimal baseAnnualRate, BigDecimal defaultProbabilityPerPeriod) {
        this.baseAnnualRate = baseAnnualRate;
        this.defaultProbabilityPerPeriod = defaultProbabilityPerPeriod;
    }

    public BigDecimal baseAnnualRate() {
        return baseAnnualRate;
    }

    public BigDecimal defaultProbabilityPerPeriod() {
        return defaultProbabilityPerPeriod;
    }

    /** Resolves a numeric scorecard total (0-100) to its letter grade. */
    public static RiskGrade fromScore(int score) {
        if (score >= 90) return A;
        if (score >= 78) return B;
        if (score >= 65) return C;
        if (score >= 52) return D;
        if (score >= 38) return E;
        if (score >= 24) return F;
        return G;
    }
}
