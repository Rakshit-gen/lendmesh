package com.lendmesh.api.service;

import com.lendmesh.api.domain.RiskGrade;
import java.util.List;

/**
 * The full output of the risk scorecard: not just a grade, but the
 * itemized point breakdown that produced it, so the UI can show a lender
 * exactly why a listing landed where it did.
 */
public record ScorecardResult(int total, RiskGrade grade, List<ScoreFactor> factors) {

    public record ScoreFactor(String label, int points, int maxPoints, String detail) {
    }
}
