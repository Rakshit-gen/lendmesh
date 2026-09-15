package com.lendmesh.api.service;

import com.lendmesh.api.domain.LedgerEntryType;
import com.lendmesh.api.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Test
    void creditAndDebitKeepTheLedgerInSyncWithTheBalance() {
        Wallet wallet = walletService.openWallet("wallet-test-user-1", new BigDecimal("500.00"));

        walletService.credit("wallet-test-user-1", new BigDecimal("100.00"), LedgerEntryType.DEPOSIT, null, "top up");
        walletService.debit("wallet-test-user-1", new BigDecimal("250.00"), LedgerEntryType.LOAN_FUNDING, "loan-x", "funded a loan");

        Wallet refreshed = walletService.requireWallet("wallet-test-user-1");
        assertThat(refreshed.getBalance()).isEqualByComparingTo("350.00");
        assertThat(walletService.history("wallet-test-user-1")).hasSize(2);
        assertThat(wallet.getUserId()).isEqualTo("wallet-test-user-1");
    }

    @Test
    void debitingMoreThanTheBalanceIsRejected() {
        walletService.openWallet("wallet-test-user-2", new BigDecimal("50.00"));

        assertThatThrownBy(() ->
                walletService.debit("wallet-test-user-2", new BigDecimal("999.00"), LedgerEntryType.LOAN_FUNDING, null, "too much"))
                .isInstanceOf(IllegalStateException.class);
    }
}
