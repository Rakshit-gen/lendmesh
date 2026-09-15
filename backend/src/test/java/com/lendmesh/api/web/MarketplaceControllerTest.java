package com.lendmesh.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendmesh.api.config.SecurityConfig;
import com.lendmesh.api.exception.ConflictException;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.domain.LoanNote;
import com.lendmesh.api.security.JwtService;
import com.lendmesh.api.service.MarketplaceService;
import com.lendmesh.api.web.dto.LoanDtos.FundRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MarketplaceController.class)
@Import(SecurityConfig.class)
class MarketplaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarketplaceService marketplaceService;

    @MockBean
    private JwtService jwtService;

    private RequestPostProcessor asUser() {
        return user("lender-1");
    }

    @Test
    void fundingWithAnAmountBelowTheMinimumIsRejected() throws Exception {
        FundRequest request = new FundRequest(new BigDecimal("0.50"));

        mockMvc.perform(post("/api/loans/loan-1/fund").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fundingAnOpenListingReturnsTheNewNote() throws Exception {
        LoanNote note = new LoanNote("loan-1", "lender-1", new BigDecimal("500.00"));
        when(marketplaceService.fund("lender-1", "loan-1", new BigDecimal("500.00"))).thenReturn(note);

        FundRequest request = new FundRequest(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/loans/loan-1/fund").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountFunded").value(500.00));
    }

    @Test
    void fundingAnUnknownListingReturns404() throws Exception {
        when(marketplaceService.fund(any(), any(), any())).thenThrow(new NotFoundException("No such loan listing"));

        FundRequest request = new FundRequest(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/loans/missing/fund").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void fundingAListingThatIsNotOpenReturns409() throws Exception {
        when(marketplaceService.fund(any(), any(), any()))
                .thenThrow(new ConflictException("Listing is not accepting funding right now"));

        FundRequest request = new FundRequest(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/loans/loan-1/fund").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
