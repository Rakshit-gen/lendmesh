package com.lendmesh.api.web;

import com.lendmesh.api.config.SecurityConfig;
import com.lendmesh.api.domain.LedgerEntry;
import com.lendmesh.api.domain.LedgerEntryType;
import com.lendmesh.api.domain.Wallet;
import com.lendmesh.api.exception.NotFoundException;
import com.lendmesh.api.security.JwtService;
import com.lendmesh.api.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
@Import(SecurityConfig.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private JwtService jwtService;

    private RequestPostProcessor asUser() {
        return user("user-1");
    }

    @Test
    void walletReturnsTheCallersBalance() throws Exception {
        Wallet wallet = new Wallet("user-1", new BigDecimal("2500.00"));
        when(walletService.requireWallet("user-1")).thenReturn(wallet);

        mockMvc.perform(get("/api/wallet").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(2500.00));
    }

    @Test
    void walletForAUserWithNoWalletYetReturns404() throws Exception {
        when(walletService.requireWallet("user-1")).thenThrow(new NotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/wallet").with(asUser()))
                .andExpect(status().isNotFound());
    }

    @Test
    void historyReturnsTheCallersLedgerEntries() throws Exception {
        LedgerEntry entry = new LedgerEntry("wallet-1", LedgerEntryType.DEPOSIT, new BigDecimal("500.00"),
                new BigDecimal("500.00"), null, "Initial top-up");
        when(walletService.history("user-1")).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/wallet/history").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(500.00));
    }
}
