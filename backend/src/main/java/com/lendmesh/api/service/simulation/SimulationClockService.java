package com.lendmesh.api.service.simulation;

import com.lendmesh.api.domain.*;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.repository.LoanNoteRepository;
import com.lendmesh.api.service.FundingUpdate;
import com.lendmesh.api.service.WalletService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The heartbeat of the simulation. Every tick advances every active loan
 * by exactly one repayment period: roll the borrower's default odds for
 * that grade, and either collect the installment and pay lenders their
 * pro-rata share, or charge the loan off and write down the remaining
 * schedule.
 *
 * ponytail: repayment cash is treated as sourced externally rather than
 * debited from a modeled borrower income stream — add real borrower
 * cashflow if defaults ever need to depend on more than the grade's
 * static probability.
 */
@Service
public class SimulationClockService {

    private final LoanListingRepository listings;
    private final InstallmentRepository installments;
    private final LoanNoteRepository notes;
    private final WalletService walletService;
    private final DefaultSimulationService defaultSimulation;
    private final SimpMessagingTemplate broadcaster;

    public SimulationClockService(LoanListingRepository listings, InstallmentRepository installments,
                                   LoanNoteRepository notes, WalletService walletService,
                                   DefaultSimulationService defaultSimulation, SimpMessagingTemplate broadcaster) {
        this.listings = listings;
        this.installments = installments;
        this.notes = notes;
        this.walletService = walletService;
        this.defaultSimulation = defaultSimulation;
        this.broadcaster = broadcaster;
    }

    @Scheduled(fixedDelayString = "${lendmesh.simulation.tick-interval-ms}")
    public void tick() {
        for (LoanListing listing : listings.findByStatus(LoanStatus.ACTIVE)) {
            advanceOnePeriod(listing);
        }
    }

    @Transactional
    public void advanceOnePeriod(LoanListing listing) {
        List<Installment> schedule = installments.findByLoanListingIdOrderByPeriodNumberAsc(listing.getId());
        Optional<Installment> next = schedule.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.SCHEDULED)
                .findFirst();
        if (next.isEmpty()) {
            return;
        }
        Installment installment = next.get();

        if (defaultSimulation.rollsDefault(listing.getRiskGrade())) {
            chargeOff(listing, schedule, installment);
        } else {
            collect(listing, installment);
        }
    }

    private void collect(LoanListing listing, Installment installment) {
        installment.markPaid();
        installments.save(installment);

        List<LoanNote> loanNotes = notes.findByLoanListingId(listing.getId());
        for (LoanNote note : loanNotes) {
            BigDecimal share = note.shareOf(listing.getFundedAmount());
            BigDecimal principalShare = installment.getPrincipalDue().multiply(share).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal interestShare = installment.getInterestDue().multiply(share).setScale(2, java.math.RoundingMode.HALF_UP);

            note.recordRepayment(principalShare, interestShare);
            notes.save(note);

            BigDecimal totalShare = principalShare.add(interestShare);
            if (totalShare.signum() > 0) {
                walletService.credit(note.getLenderId(), totalShare, LedgerEntryType.REPAYMENT_RECEIVED,
                        listing.getId(), "Repayment period " + installment.getPeriodNumber() + " for loan " + listing.getId());
            }
        }

        if (installment.getPeriodNumber() == listing.getTermMonths()) {
            listing.setStatus(LoanStatus.PAID_OFF);
            listings.save(listing);
        }

        broadcaster.convertAndSend("/topic/marketplace/" + listing.getId(),
                new FundingUpdate(listing.getId(), listing.getFundedAmount(), listing.getRequestedAmount(), listing.getStatus()));
    }

    private void chargeOff(LoanListing listing, List<Installment> schedule, Installment failedInstallment) {
        failedInstallment.markMissed();
        installments.save(failedInstallment);

        for (Installment remaining : schedule) {
            if (remaining.getStatus() == InstallmentStatus.SCHEDULED) {
                remaining.markWrittenOff();
                installments.save(remaining);
            }
        }

        for (LoanNote note : notes.findByLoanListingId(listing.getId())) {
            walletService.credit(note.getLenderId(), BigDecimal.ZERO, LedgerEntryType.CHARGE_OFF,
                    listing.getId(), "Loan " + listing.getId() + " charged off; "
                            + note.outstandingPrincipal() + " of principal not recovered");
        }

        listing.setStatus(LoanStatus.DEFAULTED);
        listings.save(listing);

        broadcaster.convertAndSend("/topic/marketplace/" + listing.getId(),
                new FundingUpdate(listing.getId(), listing.getFundedAmount(), listing.getRequestedAmount(), listing.getStatus()));
    }
}
