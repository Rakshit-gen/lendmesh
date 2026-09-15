package com.lendmesh.api.service;

import com.lendmesh.api.domain.BorrowerProfile;
import com.lendmesh.api.domain.RiskGrade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * A transparent, weighted scorecard that turns a {@link BorrowerProfile} into
 * a 0-100 total and a {@link RiskGrade}. Every point awarded is attributable
 * to one factor, on purpose: the whole idea of LendMesh is that a lender can
 * see exactly why a listing got the grade it did instead of trusting a
 * black-box number.
 *
 * Weights: debt-to-income 30, on-time payment history 25, credit
 * utilization 20, length of credit history 15, open delinquencies 10.
 */
@Component
public class RiskScoringEngine {

    public ScorecardResult score(BorrowerProfile profile) {
        List<ScorecardResult.ScoreFactor> factors = new ArrayList<>();

        int dtiPoints = scoreDebtToIncome(profile, factors);
        int paymentPoints = scoreOnTimePayments(profile, factors);
        int utilizationPoints = scoreUtilization(profile, factors);
        int historyPoints = scoreCreditHistory(profile, factors);
        int delinquencyPoints = scoreDelinquencies(profile, factors);

        int total = dtiPoints + paymentPoints + utilizationPoints + historyPoints + delinquencyPoints;
        return new ScorecardResult(total, RiskGrade.fromScore(total), factors);
    }

    private int scoreDebtToIncome(BorrowerProfile profile, List<ScorecardResult.ScoreFactor> factors) {
        BigDecimal monthlyIncome = profile.getAnnualIncome().divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        BigDecimal dti = monthlyIncome.signum() == 0
                ? BigDecimal.ONE
                : profile.getExistingMonthlyDebt().divide(monthlyIncome, 4, RoundingMode.HALF_UP);

        int points;
        if (dti.compareTo(new BigDecimal("0.10")) <= 0) points = 30;
        else if (dti.compareTo(new BigDecimal("0.20")) <= 0) points = 24;
        else if (dti.compareTo(new BigDecimal("0.30")) <= 0) points = 18;
        else if (dti.compareTo(new BigDecimal("0.40")) <= 0) points = 10;
        else if (dti.compareTo(new BigDecimal("0.50")) <= 0) points = 4;
        else points = 0;

        factors.add(new ScorecardResult.ScoreFactor(
                "Debt-to-income ratio", points, 30,
                "Existing monthly debt is " + dti.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) + "% of monthly income"));
        return points;
    }

    private int scoreOnTimePayments(BorrowerProfile profile, List<ScorecardResult.ScoreFactor> factors) {
        int rate = clamp(profile.getOnTimePaymentRate(), 0, 100);
        int points = Math.round(rate / 100f * 25);
        factors.add(new ScorecardResult.ScoreFactor(
                "On-time payment history", points, 25,
                rate + "% of past payments made on time"));
        return points;
    }

    private int scoreUtilization(BorrowerProfile profile, List<ScorecardResult.ScoreFactor> factors) {
        int utilization = clamp(profile.getCreditUtilization(), 0, 100);
        int points = 20 - Math.round(utilization / 100f * 20);
        factors.add(new ScorecardResult.ScoreFactor(
                "Credit utilization", points, 20,
                utilization + "% of available revolving credit in use"));
        return points;
    }

    private int scoreCreditHistory(BorrowerProfile profile, List<ScorecardResult.ScoreFactor> factors) {
        int months = Math.max(0, profile.getMonthsOfCreditHistory());
        int points = Math.min(15, months / 6);
        factors.add(new ScorecardResult.ScoreFactor(
                "Length of credit history", points, 15,
                months + " months on record"));
        return points;
    }

    private int scoreDelinquencies(BorrowerProfile profile, List<ScorecardResult.ScoreFactor> factors) {
        int delinquencies = Math.max(0, profile.getOpenDelinquencies());
        int points = Math.max(0, 10 - delinquencies * 4);
        factors.add(new ScorecardResult.ScoreFactor(
                "Open delinquencies", points, 10,
                delinquencies + " open delinquent account" + (delinquencies == 1 ? "" : "s")));
        return points;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
