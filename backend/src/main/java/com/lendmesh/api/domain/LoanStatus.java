package com.lendmesh.api.domain;

/** Lifecycle of a loan listing from application through payoff or charge-off. */
public enum LoanStatus {
    PENDING_REVIEW,
    OPEN_FOR_FUNDING,
    FUNDED,
    ACTIVE,
    PAID_OFF,
    DEFAULTED,
    REJECTED
}
