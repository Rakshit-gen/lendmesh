package com.lendmesh.api.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InstallmentTest {

    private Installment installment() {
        return new Installment("loan-1", 1, new BigDecimal("400.00"), new BigDecimal("50.00"), new BigDecimal("9600.00"));
    }

    @Test
    void totalDueIsPrincipalPlusInterest() {
        assertThat(installment().totalDue()).isEqualByComparingTo("450.00");
    }

    @Test
    void startsScheduledAndUnsettled() {
        Installment installment = installment();
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.SCHEDULED);
        assertThat(installment.getSettledAt()).isNull();
    }

    @Test
    void markPaidRecordsStatusAndSettlementTime() {
        Installment installment = installment();
        installment.markPaid();
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PAID);
        assertThat(installment.getSettledAt()).isNotNull();
    }

    @Test
    void markMissedRecordsStatusAndSettlementTime() {
        Installment installment = installment();
        installment.markMissed();
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.MISSED);
        assertThat(installment.getSettledAt()).isNotNull();
    }

    @Test
    void markWrittenOffRecordsStatusAndSettlementTime() {
        Installment installment = installment();
        installment.markWrittenOff();
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.WRITTEN_OFF);
        assertThat(installment.getSettledAt()).isNotNull();
    }
}
