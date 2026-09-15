package com.lendmesh.api.domain;

/** Reason a wallet balance moved. */
public enum LedgerEntryType {
    DEPOSIT,
    LOAN_FUNDING,
    LOAN_DISBURSEMENT,
    REPAYMENT_RECEIVED,
    REPAYMENT_PRINCIPAL,
    CHARGE_OFF
}
