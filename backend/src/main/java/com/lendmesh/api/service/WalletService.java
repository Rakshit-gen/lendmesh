package com.lendmesh.api.service;

import com.lendmesh.api.domain.LedgerEntry;
import com.lendmesh.api.domain.LedgerEntryType;
import com.lendmesh.api.domain.Wallet;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.LedgerEntryRepository;
import com.lendmesh.api.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Every simulated cash movement in LendMesh goes through here, so a
 * wallet's balance and its ledger history can never drift apart.
 */
@Service
public class WalletService {

    private final WalletRepository wallets;
    private final LedgerEntryRepository ledger;

    public WalletService(WalletRepository wallets, LedgerEntryRepository ledger) {
        this.wallets = wallets;
        this.ledger = ledger;
    }

    @Transactional
    public Wallet openWallet(String userId, BigDecimal openingBalance) {
        return wallets.save(new Wallet(userId, openingBalance));
    }

    public Wallet requireWallet(String userId) {
        return wallets.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No wallet for user " + userId));
    }

    @Transactional
    public Wallet credit(String userId, BigDecimal amount, LedgerEntryType type, String relatedLoanId, String memo) {
        Wallet wallet = requireWallet(userId);
        wallet.credit(amount);
        wallets.save(wallet);
        ledger.save(new LedgerEntry(wallet.getId(), type, amount, wallet.getBalance(), relatedLoanId, memo));
        return wallet;
    }

    @Transactional
    public Wallet debit(String userId, BigDecimal amount, LedgerEntryType type, String relatedLoanId, String memo) {
        Wallet wallet = requireWallet(userId);
        wallet.debit(amount);
        wallets.save(wallet);
        ledger.save(new LedgerEntry(wallet.getId(), type, amount.negate(), wallet.getBalance(), relatedLoanId, memo));
        return wallet;
    }

    public List<LedgerEntry> history(String userId) {
        Wallet wallet = requireWallet(userId);
        return ledger.findByWalletIdOrderByOccurredAtDesc(wallet.getId());
    }
}
