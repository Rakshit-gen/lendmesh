package com.lendmesh.api.service.simulation;

import com.lendmesh.api.domain.RiskGrade;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Rolls the dice on whether a borrower defaults in a given repayment
 * period. The odds are never arbitrary, they come straight from the
 * {@link RiskGrade} the scorecard already assigned, so a grade-A loan
 * really does default far less often than a grade-G one over a full
 * simulated run, the same relationship a real risk model is built on.
 */
@Component
public class DefaultSimulationService {

    private final Random random;

    public DefaultSimulationService() {
        this(new Random());
    }

    public DefaultSimulationService(Random random) {
        this.random = random;
    }

    public boolean rollsDefault(RiskGrade grade) {
        return random.nextDouble() < grade.defaultProbabilityPerPeriod().doubleValue();
    }
}
