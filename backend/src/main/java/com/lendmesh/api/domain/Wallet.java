package com.lendmesh.api.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * A simulated cash balance for one user. Every change to {@link #balance}
 * must be paired with a {@link LedgerEntry} so the number is always
 * reconstructable from history, the way a real ledger-backed account works.
 */
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private long version;

    protected Wallet() {
    }

    public Wallet(String userId, BigDecimal openingBalance) {
        this.userId = userId;
        this.balance = openingBalance;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient simulated balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public long getVersion() {
        return version;
    }
}
