package com.lendmesh.api.service;

import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.LoanNote;
import com.lendmesh.api.domain.RiskGrade;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Pure-mock companion to {@link PortfolioServiceTest}, for edge cases that don't need a real database. */
class PortfolioServiceUnitTest {

    private LoanNoteRepository notes;
    private LoanListingRepository listings;
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        notes = mock(LoanNoteRepository.class);
        listings = mock(LoanListingRepository.class);
        portfolioService = new PortfolioService(notes, listings);
    }

    @Test
    void outstandingPrincipalIsCommittedMinusRepaid() {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Loan", new BigDecimal("1000.00"),
                12, RiskGrade.B, 80, new BigDecimal("0.089"));
        LoanNote note = new LoanNote("loan-1", "lender-1", new BigDecimal("1000.00"));
        note.recordRepayment(new BigDecimal("300.00"), new BigDecimal("20.00"));
        when(notes.findByLenderId("lender-1")).thenReturn(List.of(note));
        when(listings.findById("loan-1")).thenReturn(Optional.of(listing));

        PortfolioSummary summary = portfolioService.summarize("lender-1");

        assertThat(summary.outstandingPrincipal()).isEqualByComparingTo("700.00");
        assertThat(summary.totalInterestEarned()).isEqualByComparingTo("20.00");
    }

    @Test
    void notesAcrossDifferentGradesAreCountedSeparately() {
        LoanListing gradeA = new LoanListing("borrower-1", "profile-1", "Loan A", new BigDecimal("500.00"),
                12, RiskGrade.A, 95, RiskGrade.A.baseAnnualRate());
        LoanListing gradeC = new LoanListing("borrower-2", "profile-2", "Loan C", new BigDecimal("500.00"),
                12, RiskGrade.C, 65, RiskGrade.C.baseAnnualRate());
        LoanNote noteA = new LoanNote("loan-a", "lender-1", new BigDecimal("200.00"));
        LoanNote noteC = new LoanNote("loan-c", "lender-1", new BigDecimal("300.00"));

        when(notes.findByLenderId("lender-1")).thenReturn(List.of(noteA, noteC));
        when(listings.findById("loan-a")).thenReturn(Optional.of(gradeA));
        when(listings.findById("loan-c")).thenReturn(Optional.of(gradeC));

        PortfolioSummary summary = portfolioService.summarize("lender-1");

        assertThat(summary.loansByGrade()).containsEntry("A", 1).containsEntry("C", 1);
        assertThat(summary.totalPrincipalCommitted()).isEqualByComparingTo("500.00");
    }

    @Test
    void aNoteReferencingAMissingListingFailsLoudlyInsteadOfSilentlyDroppingIt() {
        LoanNote orphanedNote = new LoanNote("deleted-loan", "lender-1", new BigDecimal("100.00"));
        when(notes.findByLenderId("lender-1")).thenReturn(List.of(orphanedNote));
        when(listings.findById("deleted-loan")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.summarize("lender-1")).isInstanceOf(NotFoundException.class);
    }
}
