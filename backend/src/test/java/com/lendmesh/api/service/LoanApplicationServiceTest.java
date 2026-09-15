package com.lendmesh.api.service;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.repository.BorrowerProfileRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanApplicationServiceTest {

    private BorrowerProfileRepository profiles;
    private LoanListingRepository listings;
    private RiskScoringEngine scoringEngine;
    private LoanApplicationService applicationService;

    @BeforeEach
    void setUp() {
        profiles = mock(BorrowerProfileRepository.class);
        listings = mock(LoanListingRepository.class);
        scoringEngine = mock(RiskScoringEngine.class);
        applicationService = new LoanApplicationService(profiles, listings, scoringEngine);

        when(profiles.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(listings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void theListingIsFrozenAtTheScorecardsGradeRateAndTotalAtApplicationTime() {
        when(scoringEngine.score(any())).thenReturn(new ScorecardResult(82, RiskGrade.B, List.of()));

        LoanListing listing = applicationService.apply("borrower-1", "Home repair", new BigDecimal("5000.00"), 24,
                new BigDecimal("60000"), new BigDecimal("500"), 90, 20, 24, 0);

        assertThat(listing.getRiskGrade()).isEqualTo(RiskGrade.B);
        assertThat(listing.getScorecardTotal()).isEqualTo(82);
        assertThat(listing.getInterestRate()).isEqualByComparingTo(RiskGrade.B.baseAnnualRate());
    }

    @Test
    void aNewApplicationIsImmediatelyOpenForFunding() {
        when(scoringEngine.score(any())).thenReturn(new ScorecardResult(50, RiskGrade.D, List.of()));

        LoanListing listing = applicationService.apply("borrower-1", "Debt consolidation", new BigDecimal("2000.00"),
                12, new BigDecimal("40000"), new BigDecimal("800"), 70, 50, 12, 1);

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.OPEN_FOR_FUNDING);
    }

    @Test
    void theBorrowerProfileIsPersistedBeforeItIsScored() {
        when(scoringEngine.score(any())).thenReturn(new ScorecardResult(60, RiskGrade.C, List.of()));

        applicationService.apply("borrower-1", "Home repair", new BigDecimal("1000.00"), 6,
                new BigDecimal("30000"), new BigDecimal("400"), 80, 30, 18, 0);

        var inOrder = inOrder(profiles, scoringEngine, listings);
        inOrder.verify(profiles).save(any());
        inOrder.verify(scoringEngine).score(any());
        inOrder.verify(listings).save(any());
    }
}
