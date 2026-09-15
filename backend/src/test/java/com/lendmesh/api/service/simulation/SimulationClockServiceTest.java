package com.lendmesh.api.service.simulation;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import com.lendmesh.api.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SimulationClockServiceTest {

    private LoanListingRepository listings;
    private InstallmentRepository installments;
    private LoanNoteRepository notes;
    private WalletService walletService;
    private DefaultSimulationService defaultSimulation;
    private SimpMessagingTemplate broadcaster;
    private SimulationClockService clockService;

    private LoanListing activeListing(BigDecimal fundedAmount, int termMonths) {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Debt consolidation",
                fundedAmount, termMonths, RiskGrade.B, 80, new BigDecimal("0.089"));
        listing.setStatus(LoanStatus.OPEN_FOR_FUNDING);
        listing.applyFunding(fundedAmount);
        return listing;
    }

    @BeforeEach
    void setUp() {
        listings = mock(LoanListingRepository.class);
        installments = mock(InstallmentRepository.class);
        notes = mock(LoanNoteRepository.class);
        walletService = mock(WalletService.class);
        defaultSimulation = mock(DefaultSimulationService.class);
        broadcaster = mock(SimpMessagingTemplate.class);
        clockService = new SimulationClockService(listings, installments, notes, walletService, defaultSimulation, broadcaster);
    }

    @Test
    void aListingWithNoScheduledInstallmentsLeftIsLeftAlone() {
        LoanListing listing = activeListing(new BigDecimal("1000.00"), 12);
        Installment paid = new Installment("loan-1", 1, new BigDecimal("80.00"), new BigDecimal("7.00"), new BigDecimal("920.00"));
        paid.markPaid();
        when(installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId())).thenReturn(List.of(paid));

        clockService.advanceOnePeriod(listing);

        verifyNoInteractions(defaultSimulation);
        verifyNoInteractions(walletService);
    }

    @Test
    void aSuccessfulPeriodPaysEachNoteHolderItsProRataShare() {
        LoanListing listing = activeListing(new BigDecimal("1000.00"), 12);
        Installment due = new Installment(listing.getId(), 1, new BigDecimal("80.00"), new BigDecimal("10.00"), new BigDecimal("920.00"));
        LoanNote note = new LoanNote(listing.getId(), "lender-1", new BigDecimal("1000.00"));

        when(installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId())).thenReturn(List.of(due));
        when(defaultSimulation.rollsDefault(RiskGrade.B)).thenReturn(false);
        when(notes.findByLoanListingId(listing.getId())).thenReturn(List.of(note));

        clockService.advanceOnePeriod(listing);

        assertThat(due.getStatus()).isEqualTo(InstallmentStatus.PAID);
        assertThat(note.getPrincipalRepaid()).isEqualByComparingTo("80.00");
        assertThat(note.getInterestRepaid()).isEqualByComparingTo("10.00");
        verify(walletService).credit(eq("lender-1"), eq(new BigDecimal("90.00")), eq(LedgerEntryType.REPAYMENT_RECEIVED), any(), any());
    }

    @Test
    void payingTheFinalInstallmentMarksTheLoanPaidOff() {
        LoanListing listing = activeListing(new BigDecimal("1000.00"), 1);
        Installment finalInstallment = new Installment(listing.getId(), 1, new BigDecimal("1000.00"), new BigDecimal("5.00"), BigDecimal.ZERO);

        when(installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId())).thenReturn(List.of(finalInstallment));
        when(defaultSimulation.rollsDefault(RiskGrade.B)).thenReturn(false);
        when(notes.findByLoanListingId(listing.getId())).thenReturn(List.of());

        clockService.advanceOnePeriod(listing);

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.PAID_OFF);
    }

    @Test
    void aDefaultRollWritesOffTheWholeRemainingScheduleAndDefaultsTheLoan() {
        LoanListing listing = activeListing(new BigDecimal("1000.00"), 3);
        Installment failing = new Installment(listing.getId(), 1, new BigDecimal("80.00"), new BigDecimal("10.00"), new BigDecimal("920.00"));
        Installment future1 = new Installment(listing.getId(), 2, new BigDecimal("80.00"), new BigDecimal("9.00"), new BigDecimal("840.00"));
        Installment future2 = new Installment(listing.getId(), 3, new BigDecimal("80.00"), new BigDecimal("8.00"), new BigDecimal("760.00"));

        when(installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId()))
                .thenReturn(List.of(failing, future1, future2));
        when(defaultSimulation.rollsDefault(RiskGrade.B)).thenReturn(true);
        when(notes.findByLoanListingId(listing.getId())).thenReturn(List.of());

        clockService.advanceOnePeriod(listing);

        assertThat(failing.getStatus()).isEqualTo(InstallmentStatus.MISSED);
        assertThat(future1.getStatus()).isEqualTo(InstallmentStatus.WRITTEN_OFF);
        assertThat(future2.getStatus()).isEqualTo(InstallmentStatus.WRITTEN_OFF);
        assertThat(listing.getStatus()).isEqualTo(LoanStatus.DEFAULTED);
    }
}
