package com.lendmesh.api.service;

import com.lendmesh.api.domain.BorrowerProfile;
import com.lendmesh.api.domain.RiskGrade;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RiskScoringEngineTest {

    private final RiskScoringEngine engine = new RiskScoringEngine();

    @Test
    void pristineProfileScoresGradeA() {
        BorrowerProfile profile = new BorrowerProfile(
                "user-1", new BigDecimal("120000"), new BigDecimal("500"),
                100, 0, 120, 0);

        ScorecardResult result = engine.score(profile);

        assertThat(result.total()).isEqualTo(100);
        assertThat(result.grade()).isEqualTo(RiskGrade.A);
        assertThat(result.factors()).hasSize(5);
    }

    @Test
    void weakProfileScoresLowGrade() {
        BorrowerProfile profile = new BorrowerProfile(
                "user-2", new BigDecimal("24000"), new BigDecimal("1800"),
                40, 95, 4, 3);

        ScorecardResult result = engine.score(profile);

        assertThat(result.total()).isLessThan(30);
        assertThat(result.grade()).isIn(RiskGrade.F, RiskGrade.G);
    }

    @Test
    void factorPointsNeverExceedTheirMax() {
        BorrowerProfile profile = new BorrowerProfile(
                "user-3", new BigDecimal("80000"), new BigDecimal("1200"),
                85, 30, 36, 1);

        ScorecardResult result = engine.score(profile);

        for (ScorecardResult.ScoreFactor factor : result.factors()) {
            assertThat(factor.points()).isBetween(0, factor.maxPoints());
        }
        int sum = result.factors().stream().mapToInt(ScorecardResult.ScoreFactor::points).sum();
        assertThat(sum).isEqualTo(result.total());
    }

    @Test
    void zeroIncomeDoesNotDivideByZero() {
        BorrowerProfile profile = new BorrowerProfile(
                "user-4", BigDecimal.ZERO, new BigDecimal("100"),
                50, 50, 12, 0);

        ScorecardResult result = engine.score(profile);

        assertThat(result.total()).isGreaterThanOrEqualTo(0);
    }
}
