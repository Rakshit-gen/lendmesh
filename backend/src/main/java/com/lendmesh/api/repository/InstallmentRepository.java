package com.lendmesh.api.repository;

import com.lendmesh.api.domain.Installment;
import com.lendmesh.api.domain.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, String> {
    List<Installment> findByLoanListingIdOrderByPeriodNumberAsc(String loanListingId);
    List<Installment> findByStatus(InstallmentStatus status);
}
