package com.lendmesh.api.web;

import com.lendmesh.api.domain.BorrowerProfile;
import com.lendmesh.api.domain.LoanListing;
import com.lendmesh.api.domain.LoanStatus;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.repository.BorrowerProfileRepository;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.service.LoanApplicationService;
import com.lendmesh.api.service.RiskScoringEngine;
import com.lendmesh.api.web.dto.LoanDtos.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanApplicationService applicationService;
    private final LoanListingRepository listings;
    private final BorrowerProfileRepository profiles;
    private final InstallmentRepository installments;
    private final RiskScoringEngine scoringEngine;

    public LoanController(LoanApplicationService applicationService, LoanListingRepository listings,
                           BorrowerProfileRepository profiles, InstallmentRepository installments,
                           RiskScoringEngine scoringEngine) {
        this.applicationService = applicationService;
        this.listings = listings;
        this.profiles = profiles;
        this.installments = installments;
        this.scoringEngine = scoringEngine;
    }

    @PostMapping("/apply")
    public ListingDetail apply(Authentication authentication, @Valid @RequestBody ApplicationRequest request) {
        LoanListing listing = applicationService.apply(
                authentication.getName(), request.purpose(), request.requestedAmount(), request.termMonths(),
                request.annualIncome(), request.existingMonthlyDebt(), request.onTimePaymentRate(),
                request.creditUtilization(), request.monthsOfCreditHistory(), request.openDelinquencies());
        return toDetail(listing);
    }

    @GetMapping
    public ListingsPage marketplace(@RequestParam(required = false) String status) {
        LoanStatus filter = status == null ? LoanStatus.OPEN_FOR_FUNDING : LoanStatus.valueOf(status);
        List<ListingSummary> summaries = listings.findByStatus(filter).stream().map(this::toSummary).toList();
        return new ListingsPage(summaries);
    }

    @GetMapping("/mine")
    public ListingsPage mine(Authentication authentication) {
        List<ListingSummary> summaries = listings.findByBorrowerId(authentication.getName())
                .stream().map(this::toSummary).toList();
        return new ListingsPage(summaries);
    }

    @GetMapping("/{id}")
    public ListingDetail detail(@PathVariable String id) {
        return toDetail(requireListing(id));
    }

    @GetMapping("/{id}/installments")
    public List<InstallmentView> schedule(@PathVariable String id) {
        return installments.findByLoanListingIdOrderByPeriodNumberAsc(id).stream()
                .map(i -> new InstallmentView(i.getPeriodNumber(), i.getPrincipalDue(), i.getInterestDue(),
                        i.getRemainingBalanceAfter(), i.getStatus().name()))
                .toList();
    }

    private LoanListing requireListing(String id) {
        return listings.findById(id).orElseThrow(() -> new NotFoundException("No such loan listing"));
    }

    private ListingSummary toSummary(LoanListing listing) {
        return new ListingSummary(listing.getId(), listing.getPurpose(), listing.getRequestedAmount(),
                listing.getFundedAmount(), listing.getTermMonths(), listing.getRiskGrade().name(),
                listing.getInterestRate(), listing.getStatus(), listing.getListedAt());
    }

    private ListingDetail toDetail(LoanListing listing) {
        BorrowerProfile profile = profiles.findById(listing.getBorrowerProfileId())
                .orElseThrow(() -> new NotFoundException("Borrower profile missing"));
        var scorecard = scoringEngine.score(profile);
        return new ListingDetail(listing.getId(), listing.getPurpose(), listing.getRequestedAmount(),
                listing.getFundedAmount(), listing.getTermMonths(), listing.getRiskGrade().name(),
                listing.getInterestRate(), listing.getStatus(), listing.getListedAt(), scorecard);
    }
}
