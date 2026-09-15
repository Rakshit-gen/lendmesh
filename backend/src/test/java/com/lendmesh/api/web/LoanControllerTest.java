package com.lendmesh.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendmesh.api.config.SecurityConfig;
import com.lendmesh.api.domain.*;
import com.lendmesh.api.repository.BorrowerProfileRepository;
import com.lendmesh.api.repository.InstallmentRepository;
import com.lendmesh.api.repository.LoanListingRepository;
import com.lendmesh.api.security.JwtService;
import com.lendmesh.api.service.LoanApplicationService;
import com.lendmesh.api.service.RiskScoringEngine;
import com.lendmesh.api.service.ScorecardResult;
import com.lendmesh.api.web.dto.LoanDtos.ApplicationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoanController.class)
@Import(SecurityConfig.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanApplicationService applicationService;

    @MockBean
    private LoanListingRepository listings;

    @MockBean
    private BorrowerProfileRepository profiles;

    @MockBean
    private InstallmentRepository installments;

    @MockBean
    private RiskScoringEngine scoringEngine;

    @MockBean
    private JwtService jwtService;

    private RequestPostProcessor asUser() {
        return user("borrower-1");
    }

    private LoanListing listing(String id, LoanStatus status) {
        LoanListing listing = new LoanListing("borrower-1", "profile-1", "Debt consolidation",
                new BigDecimal("5000.00"), 24, RiskGrade.B, 70, new BigDecimal("0.089"));
        listing.setStatus(status);
        return listing;
    }

    @Test
    void applyingWithARequestedAmountBelowTheMinimumIsRejected() throws Exception {
        ApplicationRequest request = new ApplicationRequest("Home repair", new BigDecimal("50.00"), 12,
                new BigDecimal("60000"), new BigDecimal("500"), 90, 20, 24, 0);

        mockMvc.perform(post("/api/loans/apply").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void applyingWithValidDataReturnsTheListingWithItsScorecard() throws Exception {
        LoanListing listing = listing("loan-1", LoanStatus.PENDING_REVIEW);
        BorrowerProfile profile = new BorrowerProfile("borrower-1", new BigDecimal("60000"), new BigDecimal("500"),
                90, 20, 24, 0);
        when(applicationService.apply(eq("borrower-1"), eq("Home repair"), eq(new BigDecimal("5000.00")), eq(24),
                any(), any(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(listing);
        when(profiles.findById("profile-1")).thenReturn(Optional.of(profile));
        when(scoringEngine.score(profile)).thenReturn(new ScorecardResult(70, RiskGrade.B, List.of()));

        ApplicationRequest request = new ApplicationRequest("Home repair", new BigDecimal("5000.00"), 24,
                new BigDecimal("60000"), new BigDecimal("500"), 90, 20, 24, 0);

        mockMvc.perform(post("/api/loans/apply").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purpose").value("Debt consolidation"))
                .andExpect(jsonPath("$.scorecard.total").value(70));
    }

    @Test
    void marketplaceDefaultsToOpenForFundingListings() throws Exception {
        when(listings.findByStatus(LoanStatus.OPEN_FOR_FUNDING)).thenReturn(List.of(listing("loan-1", LoanStatus.OPEN_FOR_FUNDING)));

        mockMvc.perform(get("/api/loans").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listings", hasSize(1)));
    }

    @Test
    void marketplaceWithAnUnrecognizedStatusStringMapsTo400() throws Exception {
        mockMvc.perform(get("/api/loans").with(asUser()).param("status", "NOT_A_REAL_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detailForAnUnknownListingReturns404() throws Exception {
        when(listings.findById("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loans/missing").with(asUser()))
                .andExpect(status().isNotFound());
    }
}
