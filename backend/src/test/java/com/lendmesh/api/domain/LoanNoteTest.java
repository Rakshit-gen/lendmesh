package com.lendmesh.api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanNoteTest {

    @Test
    void shareOfComputesThisNotesFractionOfTheTotalLoan() {
        LoanNote note = new LoanNote("loan-1", "lender-1", new BigDecimal("2500.00"));

        BigDecimal share = note.shareOf(new BigDecimal("10000.00"));

        assertThat(share).isEqualByComparingTo("0.2500000000");
    }

    @Test
    void recordingRepaymentsAccumulatesPrincipalAndInterestSeparately() {
        LoanNote note = new LoanNote("loan-1", "lender-1", new BigDecimal("1000.00"));

        note.recordRepayment(new BigDecimal("40.00"), new BigDecimal("7.50"));
        note.recordRepayment(new BigDecimal("41.00"), new BigDecimal("6.50"));

        assertThat(note.getPrincipalRepaid()).isEqualByComparingTo("81.00");
        assertThat(note.getInterestRepaid()).isEqualByComparingTo("14.00");
    }

    @Test
    void outstandingPrincipalShrinksAsPrincipalIsRepaid() {
        LoanNote note = new LoanNote("loan-1", "lender-1", new BigDecimal("1000.00"));

        note.recordRepayment(new BigDecimal("300.00"), BigDecimal.ZERO);

        assertThat(note.outstandingPrincipal()).isEqualByComparingTo("700.00");
    }
}
