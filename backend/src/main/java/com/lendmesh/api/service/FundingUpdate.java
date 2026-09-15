package com.lendmesh.api.service;

import com.lendmesh.api.domain.LoanStatus;

import java.math.BigDecimal;

/** Pushed over WebSocket every time a loan listing's funded amount changes. */
public record FundingUpdate(String loanListingId, BigDecimal fundedAmount, BigDecimal requestedAmount,
                             LoanStatus status) {
}
