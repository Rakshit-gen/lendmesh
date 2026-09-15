package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loan_listing")
public class LoanListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String borrowerId;

    @Column(nullable = false)
    private String borrowerProfileId;

    @Column(nullable = false, length = 200)
    private String purpose;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fundedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private int termMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskGrade riskGrade;

    @Column(nullable = false)
    private int scorecardTotal;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING_REVIEW;

    @Column(nullable = false, updatable = false)
    private Instant listedAt = Instant.now();

    private Instant fundedAt;

    @Version
    private long version;

    protected LoanListing() {
    }

    public LoanListing(String borrowerId, String borrowerProfileId, String purpose,
                        BigDecimal requestedAmount, int termMonths, RiskGrade riskGrade,
                        int scorecardTotal, BigDecimal interestRate) {
        this.borrowerId = borrowerId;
        this.borrowerProfileId = borrowerProfileId;
        this.purpose = purpose;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.riskGrade = riskGrade;
        this.scorecardTotal = scorecardTotal;
        this.interestRate = interestRate;
    }

    public BigDecimal remainingToFund() {
        return requestedAmount.subtract(fundedAmount);
    }

    public boolean isFullyFunded() {
        return fundedAmount.compareTo(requestedAmount) >= 0;
    }

    public void applyFunding(BigDecimal amount) {
        if (status != LoanStatus.OPEN_FOR_FUNDING) {
            throw new IllegalStateException("Listing is not open for funding");
        }
        if (amount.compareTo(remainingToFund()) > 0) {
            throw new IllegalArgumentException("Funding amount exceeds remaining request");
        }
        this.fundedAmount = this.fundedAmount.add(amount);
        if (isFullyFunded()) {
            this.status = LoanStatus.FUNDED;
            this.fundedAt = Instant.now();
        }
    }

    public String getId() {
        return id;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getBorrowerProfileId() {
        return borrowerProfileId;
    }

    public String getPurpose() {
        return purpose;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getFundedAmount() {
        return fundedAmount;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public RiskGrade getRiskGrade() {
        return riskGrade;
    }

    public int getScorecardTotal() {
        return scorecardTotal;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Instant getListedAt() {
        return listedAt;
    }

    public Instant getFundedAt() {
        return fundedAt;
    }

    public long getVersion() {
        return version;
    }
}
