package com.lendmesh.api.service;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.exception.ConflictException;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Where lenders commit simulated cash to a listing. Funding a loan is one
 * atomic step: debit the lender, mint their fractional note, and — the
 * moment the listing crosses 100% funded — disburse to the borrower and
 * generate the amortization schedule in the same transaction.
 */
@Service
public class MarketplaceService {

    private final LoanListingRepository listings;
    private final LoanNoteRepository notes;
    private final InstallmentRepository installments;
    private final WalletService walletService;
    private final AmortizationService amortizationService;
    private final SimpMessagingTemplate broadcaster;

    public MarketplaceService(LoanListingRepository listings, LoanNoteRepository notes,
                               InstallmentRepository installments, WalletService walletService,
                               AmortizationService amortizationService, SimpMessagingTemplate broadcaster) {
        this.listings = listings;
        this.notes = notes;
        this.installments = installments;
        this.walletService = walletService;
        this.amortizationService = amortizationService;
        this.broadcaster = broadcaster;
    }

    @Transactional
    public LoanNote fund(String lenderId, String loanListingId, BigDecimal amount) {
        LoanListing listing = listings.findById(loanListingId)
                .orElseThrow(() -> new NotFoundException("No such loan listing"));

        if (listing.getStatus() != LoanStatus.OPEN_FOR_FUNDING) {
            throw new ConflictException("Listing is not accepting funding right now");
        }

        walletService.debit(lenderId, amount, LedgerEntryType.LOAN_FUNDING, loanListingId,
                "Funded loan " + loanListingId);

        LoanNote note = notes.save(new LoanNote(loanListingId, lenderId, amount));
        listing.applyFunding(amount);
        listings.save(listing);

        broadcaster.convertAndSend("/topic/marketplace/" + loanListingId,
                new FundingUpdate(listing.getId(), listing.getFundedAmount(), listing.getRequestedAmount(), listing.getStatus()));

        if (listing.getStatus() == LoanStatus.FUNDED) {
            disburse(listing);
        }
        return note;
    }

    private void disburse(LoanListing listing) {
        walletService.credit(listing.getBorrowerId(), listing.getFundedAmount(),
                LedgerEntryType.LOAN_DISBURSEMENT, listing.getId(),
                "Disbursement for funded loan " + listing.getId());

        List<Installment> schedule = amortizationService.buildSchedule(
                listing.getId(), listing.getFundedAmount(), listing.getInterestRate(), listing.getTermMonths());
        installments.saveAll(schedule);

        listing.setStatus(LoanStatus.ACTIVE);
        listings.save(listing);

        broadcaster.convertAndSend("/topic/marketplace/" + listing.getId(),
                new FundingUpdate(listing.getId(), listing.getFundedAmount(), listing.getRequestedAmount(), listing.getStatus()));
    }
}
