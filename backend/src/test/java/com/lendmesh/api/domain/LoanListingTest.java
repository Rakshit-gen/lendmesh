package com.lendmesh.api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanListingTest {

    private LoanListing openListing(BigDecimal requested) {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Debt consolidation",
                requested, 24, RiskGrade.B, 650, new BigDecimal("0.09"));
        listing.setStatus(LoanStatus.OPEN_FOR_FUNDING);
        return listing;
    }

    @Test
    void partialFundingStaysOpenForFunding() {
        LoanListing listing = openListing(new BigDecimal("10000"));

        listing.applyFunding(new BigDecimal("4000"));

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.OPEN_FOR_FUNDING);
        assertThat(listing.remainingToFund()).isEqualByComparingTo("6000");
        assertThat(listing.isFullyFunded()).isFalse();
    }

    @Test
    void fullyFundingMovesStatusToFundedAndStampsFundedAt() {
        LoanListing listing = openListing(new BigDecimal("10000"));

        listing.applyFunding(new BigDecimal("10000"));

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.FUNDED);
        assertThat(listing.isFullyFunded()).isTrue();
        assertThat(listing.getFundedAt()).isNotNull();
    }

    @Test
    void fundingInIncrementsThatExactlyReachTheTargetCompletesTheListing() {
        LoanListing listing = openListing(new BigDecimal("10000"));

        listing.applyFunding(new BigDecimal("6000"));
        listing.applyFunding(new BigDecimal("4000"));

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.FUNDED);
        assertThat(listing.getFundedAmount()).isEqualByComparingTo("10000");
    }

    @Test
    void fundingMoreThanTheRemainingRequestIsRejected() {
        LoanListing listing = openListing(new BigDecimal("10000"));
        listing.applyFunding(new BigDecimal("7000"));

        assertThatThrownBy(() -> listing.applyFunding(new BigDecimal("4000")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(listing.getFundedAmount()).isEqualByComparingTo("7000");
    }

    @Test
    void fundingAListingThatIsNotOpenForFundingIsRejected() {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Debt consolidation",
                new BigDecimal("10000"), 24, RiskGrade.B, 650, new BigDecimal("0.09"));

        assertThatThrownBy(() -> listing.applyFunding(new BigDecimal("1000")))
                .isInstanceOf(IllegalStateException.class);
    }
}
