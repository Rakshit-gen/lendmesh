package com.lendmesh.api.service;

import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.LoanNote;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Rolls a lender's scattered fractional notes up into one portfolio view. */
@Service
public class PortfolioService {

    private final LoanNoteRepository notes;
    private final LoanListingRepository listings;

    public PortfolioService(LoanNoteRepository notes, LoanListingRepository listings) {
        this.notes = notes;
        this.listings = listings;
    }

    public PortfolioSummary summarize(String lenderId) {
        List<LoanNote> myNotes = notes.findByLenderId(lenderId);

        BigDecimal committed = BigDecimal.ZERO;
        BigDecimal principalRepaid = BigDecimal.ZERO;
        BigDecimal interestEarned = BigDecimal.ZERO;
        Map<String, Integer> byGrade = new LinkedHashMap<>();
        List<PortfolioSummary.HoldingView> holdings = new java.util.ArrayList<>();

        for (LoanNote note : myNotes) {
            LoanListing listing = listings.findById(note.getLoanListingId())
                    .orElseThrow(() -> new NotFoundException("Loan listing missing for note " + note.getId()));

            committed = committed.add(note.getPrincipalCommitted());
            principalRepaid = principalRepaid.add(note.getPrincipalRepaid());
            interestEarned = interestEarned.add(note.getInterestRepaid());
            byGrade.merge(listing.getRiskGrade().name(), 1, Integer::sum);

            holdings.add(new PortfolioSummary.HoldingView(
                    listing.getId(), listing.getRiskGrade().name(), listing.getStatus().name(),
                    note.getPrincipalCommitted(), note.getPrincipalRepaid(), note.getInterestRepaid()));
        }

        BigDecimal outstanding = committed.subtract(principalRepaid);
        return new PortfolioSummary(committed, principalRepaid, interestEarned, outstanding, byGrade, holdings);
    }
}
