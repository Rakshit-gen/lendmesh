package com.lendmesh.api.service;

import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.LoanStatus;
import com.lendmesh.api.domain.UserRole;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the full path a real listing takes: apply, score, list, get
 * funded by a lender, and disburse, using the real services wired
 * together, not mocks, so a wiring mistake anywhere in the chain fails
 * this test.
 */
@SpringBootTest
class MarketplaceFlowIntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private MarketplaceService marketplaceService;
    @Autowired
    private LoanListingRepository listings;
    @Autowired
    private InstallmentRepository installments;
    @Autowired
    private WalletService walletService;

    private String userIdFromToken(String token) {
        return jwtService.parse(token).getSubject();
    }

    @Test
    void fundingALoanInFullDisbursesAndBuildsTheSchedule() {
        String borrowerId = userIdFromToken(authService.register(
                "Priya Borrower", "priya+" + System.nanoTime() + "@example.com", "password123", Set.of(UserRole.BORROWER)));
        String lenderId = userIdFromToken(authService.register(
                "Leo Lender", "leo+" + System.nanoTime() + "@example.com", "password123", Set.of(UserRole.LENDER)));

        LoanListing listing = loanApplicationService.apply(
                borrowerId, "Debt consolidation", new BigDecimal("1000.00"), 6,
                new BigDecimal("60000"), new BigDecimal("400"), 95, 20, 48, 0);

        assertThat(listing.getStatus()).isEqualTo(LoanStatus.OPEN_FOR_FUNDING);

        BigDecimal borrowerBalanceBefore = walletService.requireWallet(borrowerId).getBalance();
        BigDecimal lenderBalanceBefore = walletService.requireWallet(lenderId).getBalance();

        marketplaceService.fund(lenderId, listing.getId(), new BigDecimal("1000.00"));

        LoanListing refreshed = listings.findById(listing.getId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(refreshed.isFullyFunded()).isTrue();

        assertThat(walletService.requireWallet(borrowerId).getBalance())
                .isEqualByComparingTo(borrowerBalanceBefore.add(new BigDecimal("1000.00")));
        assertThat(walletService.requireWallet(lenderId).getBalance())
                .isEqualByComparingTo(lenderBalanceBefore.subtract(new BigDecimal("1000.00")));

        assertThat(installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId())).hasSize(6);
    }
}
