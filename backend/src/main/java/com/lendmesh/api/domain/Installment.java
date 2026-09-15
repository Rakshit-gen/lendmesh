package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/** One row of a loan's amortization schedule. */
@Entity
@Table(name = "installment")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String loanListingId;

    @Column(nullable = false)
    private int periodNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestDue;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingBalanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status = InstallmentStatus.SCHEDULED;

    private Instant settledAt;

    protected Installment() {
    }

    public Installment(String loanListingId, int periodNumber, BigDecimal principalDue,
                        BigDecimal interestDue, BigDecimal remainingBalanceAfter) {
        this.loanListingId = loanListingId;
        this.periodNumber = periodNumber;
        this.principalDue = principalDue;
        this.interestDue = interestDue;
        this.remainingBalanceAfter = remainingBalanceAfter;
    }

    public BigDecimal totalDue() {
        return principalDue.add(interestDue);
    }

    public void markPaid() {
        this.status = InstallmentStatus.PAID;
        this.settledAt = Instant.now();
    }

    public void markMissed() {
        this.status = InstallmentStatus.MISSED;
        this.settledAt = Instant.now();
    }

    public void markWrittenOff() {
        this.status = InstallmentStatus.WRITTEN_OFF;
        this.settledAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getLoanListingId() {
        return loanListingId;
    }

    public int getPeriodNumber() {
        return periodNumber;
    }

    public BigDecimal getPrincipalDue() {
        return principalDue;
    }

    public BigDecimal getInterestDue() {
        return interestDue;
    }

    public BigDecimal getRemainingBalanceAfter() {
        return remainingBalanceAfter;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public Instant getSettledAt() {
        return settledAt;
    }
}
