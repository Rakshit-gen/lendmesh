package com.lendmesh.api.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskGradeTest {

    @Test
    void eachGradeBoundaryRoundsUpToTheBetterGrade() {
        assertThat(RiskGrade.fromScore(100)).isEqualTo(RiskGrade.A);
        assertThat(RiskGrade.fromScore(90)).isEqualTo(RiskGrade.A);
        assertThat(RiskGrade.fromScore(89)).isEqualTo(RiskGrade.B);
        assertThat(RiskGrade.fromScore(78)).isEqualTo(RiskGrade.B);
        assertThat(RiskGrade.fromScore(77)).isEqualTo(RiskGrade.C);
        assertThat(RiskGrade.fromScore(65)).isEqualTo(RiskGrade.C);
        assertThat(RiskGrade.fromScore(64)).isEqualTo(RiskGrade.D);
        assertThat(RiskGrade.fromScore(52)).isEqualTo(RiskGrade.D);
        assertThat(RiskGrade.fromScore(51)).isEqualTo(RiskGrade.E);
        assertThat(RiskGrade.fromScore(38)).isEqualTo(RiskGrade.E);
        assertThat(RiskGrade.fromScore(37)).isEqualTo(RiskGrade.F);
        assertThat(RiskGrade.fromScore(24)).isEqualTo(RiskGrade.F);
        assertThat(RiskGrade.fromScore(23)).isEqualTo(RiskGrade.G);
        assertThat(RiskGrade.fromScore(0)).isEqualTo(RiskGrade.G);
    }

    @Test
    void worseGradesCarryHigherBaseRatesAndHigherDefaultProbability() {
        RiskGrade[] gradesBestToWorst = RiskGrade.values();
        for (int i = 1; i < gradesBestToWorst.length; i++) {
            RiskGrade better = gradesBestToWorst[i - 1];
            RiskGrade worse = gradesBestToWorst[i];
            assertThat(worse.baseAnnualRate()).isGreaterThan(better.baseAnnualRate());
            assertThat(worse.defaultProbabilityPerPeriod()).isGreaterThan(better.defaultProbabilityPerPeriod());
        }
    }
}
