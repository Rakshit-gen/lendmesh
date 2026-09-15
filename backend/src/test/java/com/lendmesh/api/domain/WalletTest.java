package com.lendmesh.api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WalletTest {

    @Test
    void creditIncreasesBalance() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("1000.00"));
        wallet.credit(new BigDecimal("250.00"));
        assertThat(wallet.getBalance()).isEqualByComparingTo("1250.00");
    }

    @Test
    void debitDecreasesBalance() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("1000.00"));
        wallet.debit(new BigDecimal("400.00"));
        assertThat(wallet.getBalance()).isEqualByComparingTo("600.00");
    }

    @Test
    void debitBeyondBalanceIsRejected() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("100.00"));
        assertThatThrownBy(() -> wallet.debit(new BigDecimal("150.00")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debitingExactlyTheFullBalanceLeavesItAtZeroRatherThanRejecting() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("500.00"));
        wallet.debit(new BigDecimal("500.00"));
        assertThat(wallet.getBalance()).isEqualByComparingTo("0.00");
    }
}
