package com.lendmesh.api.repository;

import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanListingRepository extends JpaRepository<LoanListing, String> {
    List<LoanListing> findByStatus(LoanStatus status);
    List<LoanListing> findByStatusIn(List<LoanStatus> statuses);
    List<LoanListing> findByBorrowerId(String borrowerId);
}
