package com.lendmesh.api.service;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.exception.ConflictException;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MarketplaceServiceTest {

    private LoanListingRepository listings;
    private LoanNoteRepository notes;
    private InstallmentRepository installments;
    private WalletService walletService;
    private AmortizationService amortizationService;
    private SimpMessagingTemplate broadcaster;
    private MarketplaceService marketplaceService;

    private LoanListing listing(BigDecimal requested) {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Debt consolidation",
                requested, 12, RiskGrade.B, 80, new BigDecimal("0.089"));
        listing.setStatus(LoanStatus.OPEN_FOR_FUNDING);
        return listing;
    }

    @BeforeEach
    void setUp() {
        listings = mock(LoanListingRepository.class);
        notes = mock(LoanNoteRepository.class);
        installments = mock(InstallmentRepository.class);
        walletService = mock(WalletService.class);
        amortizationService = mock(AmortizationService.class);
        broadcaster = mock(SimpMessagingTemplate.class);
        marketplaceService = new MarketplaceService(listings, notes, installments, walletService, amortizationService, broadcaster);

        when(notes.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(listings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void fundingAnUnknownListingIsRejected() {
        when(listings.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> marketplaceService.fund("lender-1", "missing", new BigDecimal("100")))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(walletService);
    }

    @Test
    void fundingAListingThatIsNotOpenIsRejected() {
        LoanListing listing = listing(new BigDecimal("1000.00"));
        listing.setStatus(LoanStatus.ACTIVE);
        when(listings.findById("loan-1")).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> marketplaceService.fund("lender-1", "loan-1", new BigDecimal("100")))
                .isInstanceOf(ConflictException.class);
        verifyNoInteractions(walletService);
    }

    @Test
    void partialFundingDebitsTheLenderButDoesNotDisburseYet() {
        LoanListing listing = listing(new BigDecimal("1000.00"));
        when(listings.findById("loan-1")).thenReturn(Optional.of(listing));

        LoanNote note = marketplaceService.fund("lender-1", "loan-1", new BigDecimal("400.00"));

        assertThat(note.getPrincipalCommitted()).isEqualByComparingTo("400.00");
        assertThat(listing.getStatus()).isEqualTo(LoanStatus.OPEN_FOR_FUNDING);
        verify(walletService).debit(eq("lender-1"), eq(new BigDecimal("400.00")), eq(LedgerEntryType.LOAN_FUNDING), eq("loan-1"), any());
        verifyNoMoreInteractions(amortizationService);
    }

    @Test
    void reachingFullFundingDisbursesToTheBorrowerAndActivatesTheLoan() {
        LoanListing listing = listing(new BigDecimal("1000.00"));
        when(listings.findById("loan-1")).thenReturn(Optional.of(listing));
        when(amortizationService.buildSchedule(any(), any(), any(), anyInt())).thenReturn(List.of());

        marketplaceService.fund("lender-1", "loan-1", new BigDecimal("1000.00"));

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(walletService).credit(eq("borrower-1"), eq(new BigDecimal("1000.00")),
                eq(LedgerEntryType.LOAN_DISBURSEMENT), any(), any());
        verify(installments).saveAll(any());
    }
}
