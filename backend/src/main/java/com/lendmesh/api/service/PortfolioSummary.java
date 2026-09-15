package com.lendmesh.api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record PortfolioSummary(BigDecimal totalPrincipalCommitted, BigDecimal totalPrincipalRepaid,
                                BigDecimal totalInterestEarned, BigDecimal outstandingPrincipal,
                                Map<String, Integer> loansByGrade, List<HoldingView> holdings) {

    public record HoldingView(String loanListingId, String riskGrade, String status,
                               BigDecimal principalCommitted, BigDecimal principalRepaid,
                               BigDecimal interestRepaid) {
    }
}
