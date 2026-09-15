package com.lendmesh.api.service;

import com.lendmesh.api.domain.LedgerEntryType;
import com.lendmesh.api.domain.Wallet;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.LedgerEntryRepository;
import com.lendmesh.api.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Pure-mock companion to {@link WalletServiceTest}, for behavior that doesn't need a real database. */
class WalletServiceUnitTest {

    private WalletRepository wallets;
    private LedgerEntryRepository ledger;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        wallets = mock(WalletRepository.class);
        ledger = mock(LedgerEntryRepository.class);
        walletService = new WalletService(wallets, ledger);

        when(wallets.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void requireWalletFailsLoudlyForAUserWithNone() {
        when(wallets.findByUserId("user-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.requireWallet("user-1")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void debitRecordsTheLedgerEntryAsANegativeAmount() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("1000.00"));
        when(wallets.findByUserId("user-1")).thenReturn(Optional.of(wallet));

        walletService.debit("user-1", new BigDecimal("200.00"), LedgerEntryType.LOAN_FUNDING, "loan-1", "Funded a loan");

        assertThat(wallet.getBalance()).isEqualByComparingTo("800.00");
        verify(ledger).save(argThat(entry -> entry.getAmount().compareTo(new BigDecimal("-200.00")) == 0));
    }

    @Test
    void creditRecordsTheLedgerEntryAsAPositiveAmount() {
        Wallet wallet = new Wallet("user-1", new BigDecimal("1000.00"));
        when(wallets.findByUserId("user-1")).thenReturn(Optional.of(wallet));

        walletService.credit("user-1", new BigDecimal("50.00"), LedgerEntryType.REPAYMENT_RECEIVED, "loan-1", "Repayment");

        assertThat(wallet.getBalance()).isEqualByComparingTo("1050.00");
        verify(ledger).save(argThat(entry -> entry.getAmount().compareTo(new BigDecimal("50.00")) == 0));
    }

    @Test
    void historyLooksUpTheWalletFirstSoAnUnknownUserFailsRatherThanReturningEmpty() {
        when(wallets.findByUserId("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.history("ghost")).isInstanceOf(NotFoundException.class);
    }
}
