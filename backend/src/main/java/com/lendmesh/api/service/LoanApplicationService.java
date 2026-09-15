package com.lendmesh.api.service;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.repository.BorrowerProfileRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Turns a borrower's application into a scored, listed loan. Scoring
 * happens once, at listing time, the grade a lender sees is frozen for
 * the life of the loan, the same way a real credit decision isn't
 * silently re-run mid-loan.
 */
@Service
public class LoanApplicationService {

    private final BorrowerProfileRepository profiles;
    private final LoanListingRepository listings;
    private final RiskScoringEngine scoringEngine;

    public LoanApplicationService(BorrowerProfileRepository profiles, LoanListingRepository listings,
                                   RiskScoringEngine scoringEngine) {
        this.profiles = profiles;
        this.listings = listings;
        this.scoringEngine = scoringEngine;
    }

    @Transactional
    public LoanListing apply(String borrowerId, String purpose, BigDecimal requestedAmount, int termMonths,
                              BigDecimal annualIncome, BigDecimal existingMonthlyDebt, int onTimePaymentRate,
                              int creditUtilization, int monthsOfCreditHistory, int openDelinquencies) {

        BorrowerProfile profile = profiles.save(new BorrowerProfile(
                borrowerId, annualIncome, existingMonthlyDebt, onTimePaymentRate,
                creditUtilization, monthsOfCreditHistory, openDelinquencies));

        ScorecardResult scorecard = scoringEngine.score(profile);

        LoanListing listing = new LoanListing(
                borrowerId, profile.getId(), purpose, requestedAmount, termMonths,
                scorecard.grade(), scorecard.total(), scorecard.grade().baseAnnualRate());
        listing.setStatus(LoanStatus.OPEN_FOR_FUNDING);

        return listings.save(listing);
    }
}
