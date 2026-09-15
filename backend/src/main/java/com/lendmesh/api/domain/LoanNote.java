package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/** One lender's fractional stake in a {@link LoanListing}. */
@Entity
@Table(name = "loan_note")
public class LoanNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String loanListingId;

    @Column(nullable = false)
    private String lenderId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalCommitted;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalRepaid = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal interestRepaid = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private Instant fundedAt = Instant.now();

    protected LoanNote() {
    }

    public LoanNote(String loanListingId, String lenderId, BigDecimal principalCommitted) {
        this.loanListingId = loanListingId;
        this.lenderId = lenderId;
        this.principalCommitted = principalCommitted;
    }

    /** This note's share of the loan, used to split each installment pro-rata. */
    public BigDecimal shareOf(BigDecimal loanTotalPrincipal) {
        return principalCommitted.divide(loanTotalPrincipal, 10, java.math.RoundingMode.HALF_UP);
    }

    public void recordRepayment(BigDecimal principalPortion, BigDecimal interestPortion) {
        this.principalRepaid = this.principalRepaid.add(principalPortion);
        this.interestRepaid = this.interestRepaid.add(interestPortion);
    }

    public BigDecimal outstandingPrincipal() {
        return principalCommitted.subtract(principalRepaid);
    }

    public String getId() {
        return id;
    }

    public String getLoanListingId() {
        return loanListingId;
    }

    public String getLenderId() {
        return lenderId;
    }

    public BigDecimal getPrincipalCommitted() {
        return principalCommitted;
    }

    public BigDecimal getPrincipalRepaid() {
        return principalRepaid;
    }

    public BigDecimal getInterestRepaid() {
        return interestRepaid;
    }

    public Instant getFundedAt() {
        return fundedAt;
    }
}
