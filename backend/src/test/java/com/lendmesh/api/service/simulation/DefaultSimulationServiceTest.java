package com.lendmesh.api.service.simulation;

import com.lendmesh.api.domain.RiskGrade;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSimulationServiceTest {

    private Random fixedRoll(double value) {
        return new Random() {
            @Override
            public double nextDouble() {
                return value;
            }
        };
    }

    @Test
    void rollBelowGradeProbabilityIsADefault() {
        DefaultSimulationService service = new DefaultSimulationService(fixedRoll(0.0001));
        assertThat(service.rollsDefault(RiskGrade.G)).isTrue();
    }

    @Test
    void rollAboveGradeProbabilityIsNotADefault() {
        DefaultSimulationService service = new DefaultSimulationService(fixedRoll(0.9999));
        assertThat(service.rollsDefault(RiskGrade.A)).isFalse();
    }

    @Test
    void gradeAIsSaferThanGradeGAtTheSameRoll() {
        double middleRoll = 0.05;
        DefaultSimulationService service = new DefaultSimulationService(fixedRoll(middleRoll));

        assertThat(service.rollsDefault(RiskGrade.A)).isFalse();
        assertThat(service.rollsDefault(RiskGrade.G)).isTrue();
    }
}
