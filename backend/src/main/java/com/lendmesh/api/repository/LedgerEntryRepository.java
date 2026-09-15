package com.lendmesh.api.repository;

import com.lendmesh.api.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, String> {
    List<LedgerEntry> findByWalletIdOrderByOccurredAtDesc(String walletId);
}
