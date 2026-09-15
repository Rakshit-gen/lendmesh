package com.lendmesh.api.domain;

/** State of one scheduled installment in a loan's amortization table. */
public enum InstallmentStatus {
    SCHEDULED,
    PAID,
    MISSED,
    WRITTEN_OFF
}
