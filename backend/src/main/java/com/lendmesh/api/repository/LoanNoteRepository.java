package com.lendmesh.api.repository;

import com.lendmesh.api.domain.LoanNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanNoteRepository extends JpaRepository<LoanNote, String> {
    List<LoanNote> findByLoanListingId(String loanListingId);
    List<LoanNote> findByLenderId(String lenderId);
}
