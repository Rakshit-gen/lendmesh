package com.lendmesh.api.service;

import com.lendmesh.api.domain.Installment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizationServiceTest {

    private final AmortizationService service = new AmortizationService();

    @Test
    void finalInstallmentZeroesTheBalance() {
        List<Installment> schedule = service.buildSchedule(
                "loan-1", new BigDecimal("10000"), new BigDecimal("0.12"), 24);

        assertThat(schedule).hasSize(24);
        assertThat(schedule.get(23).getRemainingBalanceAfter()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void interestShareShrinksAsBalanceComesDown() {
        List<Installment> schedule = service.buildSchedule(
                "loan-2", new BigDecimal("5000"), new BigDecimal("0.18"), 12);

        assertThat(schedule.get(0).getInterestDue())
                .isGreaterThan(schedule.get(11).getInterestDue());
    }

    @Test
    void zeroRateSplitsPrincipalEvenly() {
        List<Installment> schedule = service.buildSchedule(
                "loan-3", new BigDecimal("1200"), BigDecimal.ZERO, 12);

        for (Installment installment : schedule) {
            assertThat(installment.getInterestDue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(installment.getPrincipalDue()).isEqualByComparingTo(new BigDecimal("100.0000"));
        }
    }

    @Test
    void periodNumbersAreSequentialAndEveryRowIsStampedWithTheLoanId() {
        List<Installment> schedule = service.buildSchedule("loan-5", new BigDecimal("3000"), new BigDecimal("0.10"), 6);

        for (int i = 0; i < schedule.size(); i++) {
            assertThat(schedule.get(i).getPeriodNumber()).isEqualTo(i + 1);
            assertThat(schedule.get(i).getLoanListingId()).isEqualTo("loan-5");
        }
    }

    @Test
    void aSinglePeriodLoanPaysOffTheEntireBalanceInOneInstallment() {
        List<Installment> schedule = service.buildSchedule("loan-6", new BigDecimal("1000"), new BigDecimal("0.12"), 1);

        assertThat(schedule).hasSize(1);
        assertThat(schedule.get(0).getPrincipalDue()).isEqualByComparingTo("1000.0000");
        assertThat(schedule.get(0).getRemainingBalanceAfter()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void sumOfPrincipalPaymentsEqualsOriginalPrincipal() {
        BigDecimal principal = new BigDecimal("7500");
        List<Installment> schedule = service.buildSchedule("loan-4", principal, new BigDecimal("0.09"), 36);

        BigDecimal totalPrincipal = schedule.stream()
                .map(Installment::getPrincipalDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalPrincipal).isCloseTo(principal, org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }
}
