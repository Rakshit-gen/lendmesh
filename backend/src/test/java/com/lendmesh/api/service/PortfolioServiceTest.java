package com.lendmesh.api.service;

import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PortfolioServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private com.lendmesh.api.security.JwtService jwtService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private MarketplaceService marketplaceService;
    @Autowired
    private PortfolioService portfolioService;

    private String userIdFromToken(String token) {
        return jwtService.parse(token).getSubject();
    }

    @Test
    void summaryAggregatesAcrossMultipleNotes() {
        String borrowerId = userIdFromToken(authService.register(
                "Portfolio Borrower", "pborrow+" + System.nanoTime() + "@example.com", "password123", Set.of(UserRole.BORROWER)));
        String lenderId = userIdFromToken(authService.register(
                "Portfolio Lender", "plend+" + System.nanoTime() + "@example.com", "password123", Set.of(UserRole.LENDER)));

        LoanListing loanA = loanApplicationService.apply(borrowerId, "Loan A", new BigDecimal("400.00"), 6,
                new BigDecimal("60000"), new BigDecimal("300"), 95, 15, 48, 0);
        LoanListing loanB = loanApplicationService.apply(borrowerId, "Loan B", new BigDecimal("600.00"), 12,
                new BigDecimal("40000"), new BigDecimal("900"), 60, 70, 12, 2);

        marketplaceService.fund(lenderId, loanA.getId(), new BigDecimal("400.00"));
        marketplaceService.fund(lenderId, loanB.getId(), new BigDecimal("250.00"));

        PortfolioSummary summary = portfolioService.summarize(lenderId);

        assertThat(summary.totalPrincipalCommitted()).isEqualByComparingTo("650.00");
        assertThat(summary.holdings()).hasSize(2);
        assertThat(summary.loansByGrade().values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(2);
    }

    @Test
    void lenderWithNoNotesGetsAnEmptySummary() {
        String lenderId = userIdFromToken(authService.register(
                "Idle Lender", "idle+" + System.nanoTime() + "@example.com", "password123", Set.of(UserRole.LENDER)));

        PortfolioSummary summary = portfolioService.summarize(lenderId);

        assertThat(summary.holdings()).isEmpty();
        assertThat(summary.totalPrincipalCommitted()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
