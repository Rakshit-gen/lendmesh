package com.lendmesh.api.service;

import com.lendmesh.api.domain.Installment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a reducing-balance (annuity) amortization schedule: every period
 * pays the same total installment, with the interest share shrinking and
 * the principal share growing as the outstanding balance comes down.
 */
@Component
public class AmortizationService {

    private static final MathContext MC = new MathContext(12);

    public List<Installment> buildSchedule(String loanListingId, BigDecimal principal,
                                            BigDecimal annualRate, int termMonths) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), MC);
        BigDecimal payment = monthlyPayment(principal, monthlyRate, termMonths);

        List<Installment> schedule = new ArrayList<>(termMonths);
        BigDecimal balance = principal;

        for (int period = 1; period <= termMonths; period++) {
            BigDecimal interestDue = balance.multiply(monthlyRate, MC).setScale(4, RoundingMode.HALF_UP);
            BigDecimal principalDue = period == termMonths
                    ? balance.setScale(4, RoundingMode.HALF_UP)
                    : payment.subtract(interestDue).setScale(4, RoundingMode.HALF_UP);
            balance = balance.subtract(principalDue).setScale(4, RoundingMode.HALF_UP);

            schedule.add(new Installment(loanListingId, period, principalDue, interestDue, balance));
        }
        return schedule;
    }

    /** Standard annuity payment formula: P * r / (1 - (1+r)^-n), with a zero-rate fallback. */
    private BigDecimal monthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), MC);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(onePlusR.pow(termMonths, MC), MC));
        return principal.multiply(monthlyRate, MC).divide(discountFactor, MC);
    }
}
