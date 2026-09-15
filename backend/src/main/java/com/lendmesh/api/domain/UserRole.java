package com.lendmesh.api.domain;

/** A user can be a borrower, a lender, or both — the role only gates which actions they see. */
public enum UserRole {
    BORROWER,
    LENDER,
    ADMIN
}
