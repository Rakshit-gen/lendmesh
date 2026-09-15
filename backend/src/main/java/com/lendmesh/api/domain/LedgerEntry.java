package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/** One immutable line in a wallet's transaction history. */
@Entity
@Table(name = "ledger_entry")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerEntryType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    private String relatedLoanId;

    @Column(nullable = false)
    private String memo;

    @Column(nullable = false, updatable = false)
    private Instant occurredAt = Instant.now();

    protected LedgerEntry() {
    }

    public LedgerEntry(String walletId, LedgerEntryType type, BigDecimal amount,
                        BigDecimal balanceAfter, String relatedLoanId, String memo) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedLoanId = relatedLoanId;
        this.memo = memo;
    }

    public String getId() {
        return id;
    }

    public String getWalletId() {
        return walletId;
    }

    public LedgerEntryType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getRelatedLoanId() {
        return relatedLoanId;
    }

    public String getMemo() {
        return memo;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
