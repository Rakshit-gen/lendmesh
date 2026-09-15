package com.lendmesh.api.web.dto;

import com.lendmesh.api.domain.LoanStatus;
import com.lendmesh.api.service.ScorecardResult;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class LoanDtos {

    private LoanDtos() {
    }

    public record ApplicationRequest(
            @NotBlank @Size(max = 200) String purpose,
            @NotNull @DecimalMin("100.00") @DecimalMax("100000.00") BigDecimal requestedAmount,
            @Min(3) @Max(60) int termMonths,
            @NotNull @DecimalMin("0.00") BigDecimal annualIncome,
            @NotNull @DecimalMin("0.00") BigDecimal existingMonthlyDebt,
            @Min(0) @Max(100) int onTimePaymentRate,
            @Min(0) @Max(100) int creditUtilization,
            @Min(0) int monthsOfCreditHistory,
            @Min(0) int openDelinquencies) {
    }

    public record FundRequest(@NotNull @DecimalMin("1.00") BigDecimal amount) {
    }

    public record ListingSummary(String id, String purpose, BigDecimal requestedAmount, BigDecimal fundedAmount,
                                  int termMonths, String riskGrade, BigDecimal interestRate, LoanStatus status,
                                  Instant listedAt) {
    }

    public record ListingDetail(String id, String purpose, BigDecimal requestedAmount, BigDecimal fundedAmount,
                                 int termMonths, String riskGrade, BigDecimal interestRate, LoanStatus status,
                                 Instant listedAt, ScorecardResult scorecard) {
    }

    public record InstallmentView(int periodNumber, BigDecimal principalDue, BigDecimal interestDue,
                                   BigDecimal remainingBalanceAfter, String status) {
    }

    public record ListingsPage(List<ListingSummary> listings) {
    }
}
